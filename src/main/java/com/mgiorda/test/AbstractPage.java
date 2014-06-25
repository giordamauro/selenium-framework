package com.mgiorda.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.ISuite;

import com.mgiorda.annotation.PageContext;
import com.mgiorda.annotation.PageProperties;
import com.mgiorda.annotation.PageURL;
import com.mgiorda.common.SpringUtil;
import com.mgiorda.page.Browser;
import com.mgiorda.page.WebDriverFactory;

public abstract class AbstractPage extends ProtectedPageClasses {

	protected final Log logger = LogFactory.getLog(this.getClass());

	private static final String SUITE_CONTEXT_PROPERTY = "suite.context";
	private static final String SUITE_TIMEOUT_PROPERTY = "${suite.waitTimeOut}";
	private static final String SUITE_BROWSER_PROPERTY = "${suite.browser}";
	private static final String SUITE_ACTION_TIME_PROPERTY = "${suite.afterActionTime}";

	private final ApplicationContext applicationContext;

	private final PageElementHandler elementHandler;
	private final DriverActionHandler driverHandler;

	private final Browser browser;

	protected AbstractPage(String url) {

		if (url == null) {

			Class<?> pageClass = this.getClass();
			PageURL annotation = pageClass.getAnnotation(PageURL.class);
			if (annotation == null) {
				throw new IllegalStateException("Cannot instantiate Page whitout String url constructor parameter or @PageURL class annotation");
			}

			url = annotation.value();
		}

		String[] locations = getContextLocations();
		if (locations.length == 0) {
			Properties suiteProperties = TestThreadPoolManager.getSuitePropertiesForPage();
			locations = new String[] { "classpath*:" + suiteProperties.getProperty(SUITE_CONTEXT_PROPERTY) };
		}

		this.applicationContext = new GenericXmlApplicationContext(locations);
		initPageContext();

		WebDriverFactory driverFactory = applicationContext.getBean(WebDriverFactory.class);
		this.browser = getPropertyPlaceholder(SUITE_BROWSER_PROPERTY, Browser.class);
		WebDriver driver = driverFactory.getNewDriver(browser);

		long timeOutInSeconds = getPropertyPlaceholder(SUITE_TIMEOUT_PROPERTY, Long.class);
		WebDriverWait driverWait = new WebDriverWait(driver, timeOutInSeconds);

		long afterActionTime = getPropertyPlaceholder(SUITE_ACTION_TIME_PROPERTY, Long.class);

		this.elementHandler = new PageElementHandler(driver, driverWait, afterActionTime);
		elementHandler.setApplicationContext(applicationContext);

		String pageUrl = applicationContext.getEnvironment().resolvePlaceholders(url);

		this.driverHandler = new DriverActionHandler(driverWait, driver);

		driverHandler.goToUrl(this, pageUrl);
		AnnotationsSupport.initLocators(this);
	}

	protected AbstractPage() {
		this((String) null);
	}

	AbstractPage(AbstractPage parentPage, PageElement pageElement) {

		String[] locations = getContextLocations();
		if (locations.length == 0) {
			this.applicationContext = parentPage.applicationContext;
		} else {
			this.applicationContext = new GenericXmlApplicationContext(locations);
		}

		initPageContext();
		AnnotationsSupport.initLocators(this);

		this.driverHandler = parentPage.driverHandler;
		this.elementHandler = new PageElementHandler(parentPage.elementHandler, pageElement);
		this.browser = parentPage.browser;
	}

	protected AbstractPage(AbstractPage parentPage, String url) {

		String[] locations = getContextLocations();
		if (locations.length == 0) {
			this.applicationContext = parentPage.applicationContext;
		} else {
			this.applicationContext = new GenericXmlApplicationContext(locations);
		}

		initPageContext();

		this.driverHandler = parentPage.driverHandler;
		this.elementHandler = new PageElementHandler(driverHandler.getDriver(), driverHandler.getDriverWait(), parentPage.getElementHandler().getAfterActionTime());
		this.browser = parentPage.browser;

		if (url == null) {

			Class<?> pageClass = this.getClass();
			PageURL annotation = pageClass.getAnnotation(PageURL.class);
			if (annotation != null) {
				url = annotation.value();
			}
		}

		if (url != null) {
			String pageUrl = applicationContext.getEnvironment().resolvePlaceholders(url);
			driverHandler.goToUrl(this, pageUrl);
		} else {
			driverHandler.waitForPageToLoad(this);
		}
		AnnotationsSupport.initLocators(this);
	}

	protected AbstractPage(AbstractPage parentPage) {
		this(parentPage, (String) null);
	}

	public String getTitle() {
		return driverHandler.getTitle();
	}

	protected void waitForExecution(int seconds) {
		try {
			Thread.sleep(1000 * seconds);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	void onTestFail() {

		ISuite currentTestSuite = TestThreadPoolManager.getCurrentTestSuite();

		Long currentTime = Long.valueOf(0);
		// So that never would be two photos with same time stamp
		synchronized (currentTime) {
			currentTime = new Date().getTime();
		}

		String filePath = currentTestSuite.getOutputDirectory() + File.separator + "fail-photos" + File.separator + browser + File.separator + currentTime + ".png";

		driverHandler.takeScreenShot(filePath);
	}

	void onTestFinish() {
		driverHandler.quit();
	}

	ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	PageElementHandler getElementHandler() {
		return elementHandler;
	}

	private void initPageContext() {

		Properties suiteProperties = TestThreadPoolManager.getSuitePropertiesForPage();
		SpringUtil.addProperties(applicationContext, suiteProperties);

		addPageProperties();

		SpringUtil.autowireBean(applicationContext, this);
		TestThreadPoolManager.registerPage(this);
	}

	void addPageProperties() {

		Class<?> pageClass = this.getClass();

		InputStream resource = pageClass.getResourceAsStream(pageClass.getSimpleName() + ".properties");
		if (resource != null) {
			Properties properties = new Properties();
			try {
				properties.load(resource);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			SpringUtil.addProperties(applicationContext, properties);
		}

		PageProperties annotation = pageClass.getAnnotation(PageProperties.class);
		if (annotation != null) {

			String[] values = annotation.value();

			for (String propertySource : values) {
				SpringUtil.addPropertiesFile(applicationContext, propertySource);
			}
		}
	}

	private String[] getContextLocations() {

		String[] contextLocations = {};

		Class<?> pageClass = this.getClass();
		PageContext annotation = pageClass.getAnnotation(PageContext.class);
		if (annotation != null) {
			contextLocations = annotation.value();

			if (contextLocations.length == 0) {

				String path = pageClass.getName().replaceAll(".", "/");
				String contextFile = "classpath*:/" + path + "-context.xml";

				contextLocations = new String[] { contextFile };
			}
		}

		return contextLocations;
	}

	public static <T extends AbstractPage> T factory(Class<T> elementClass, AbstractPage page, PageElement pageElement) {
		try {
			Constructor<T> constructor = elementClass.getConstructor(AbstractPage.class, PageElement.class);
			boolean accessible = constructor.isAccessible();

			constructor.setAccessible(true);
			T newInstance = constructor.newInstance(page, pageElement);
			constructor.setAccessible(accessible);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private <T> T getPropertyPlaceholder(String property, Class<T> propertyClass) {

		String propertyValue = applicationContext.getEnvironment().resolvePlaceholders(property);

		try {
			Method method = propertyClass.getMethod("valueOf", String.class);

			@SuppressWarnings("unchecked")
			T returnValue = (T) method.invoke(null, propertyValue);

			return returnValue;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
