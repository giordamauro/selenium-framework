package com.mgiorda.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.ISuite;

import com.mgiorda.annotations.PageContext;
import com.mgiorda.annotations.PageProperties;
import com.mgiorda.annotations.PageURL;
import com.mgiorda.commons.SpringUtil;
import com.mgiorda.page.Browser;
import com.mgiorda.page.WebDriverFactory;

public abstract class AbstractPage extends ProtectedPageClasses {

	protected final Log logger = LogFactory.getLog(this.getClass());

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
			// TODO change for original-suite file, or one in default
			locations = new String[] { "classpath*:suite-context.xml" };
		}

		this.applicationContext = new GenericXmlApplicationContext(locations);
		initPageContext();

		WebDriverFactory driverFactory = applicationContext.getBean(WebDriverFactory.class);
		String browserProperty = applicationContext.getEnvironment().resolvePlaceholders("${suite.browser}");
		this.browser = Browser.valueOf(browserProperty);
		WebDriver driver = driverFactory.getNewDriver(browser);

		String waitTimeOutProperty = applicationContext.getEnvironment().resolvePlaceholders("${suite.waitTimeOut}");
		long timeOutInSeconds = Long.valueOf(waitTimeOutProperty);
		WebDriverWait driverWait = new WebDriverWait(driver, timeOutInSeconds);

		this.elementHandler = new PageElementHandler(driver, driverWait);
		elementHandler.setApplicationContext(applicationContext);

		String pageUrl = applicationContext.getEnvironment().resolvePlaceholders(url);

		this.driverHandler = new DriverActionHandler(driverWait, driver);

		driverHandler.goToUrl(pageUrl);
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
		AnnotationsSupport.initLocators(this);

		this.driverHandler = parentPage.driverHandler;
		this.elementHandler = new PageElementHandler(driverHandler.getDriver(), driverHandler.getDriverWait());
		this.browser = parentPage.browser;

		if (url == null) {

			Class<?> pageClass = this.getClass();
			PageURL annotation = pageClass.getAnnotation(PageURL.class);
			if (annotation != null) {
				url = annotation.value();
			}
		}

		if (url != null) {
			driverHandler.goToUrl(url);
		} else {
			driverHandler.waitForPageToLoad();
		}
	}

	protected AbstractPage(AbstractPage parentPage) {
		this(parentPage, (String) null);
	}

	public String getTitle() {
		return driverHandler.getTitle();
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
}
