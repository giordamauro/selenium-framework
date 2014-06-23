package com.mgiorda.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.ISuite;

import com.mgiorda.annotations.PageContext;
import com.mgiorda.annotations.PageProperties;
import com.mgiorda.annotations.PageURL;
import com.mgiorda.commons.SpringUtil;

public abstract class AbstractPage extends ProtectedPageClasses {

	protected final Log logger = LogFactory.getLog(this.getClass());

	private ApplicationContext applicationContext;

	@Autowired
	private PageElementHandler elementHandler;

	@Autowired
	private DriverActionHandler driverHandler;

	protected AbstractPage(String url) {

		if (url == null) {

			Class<?> pageClass = this.getClass();
			PageURL annotation = pageClass.getAnnotation(PageURL.class);
			if (annotation == null) {
				throw new IllegalStateException("Cannot instantiate Page whitout String url constructor parameter or @PageURL class annotation");
			}

			url = annotation.value();
		}

		initPageContext();
		elementHandler.setApplicationContext(applicationContext);

		String pageUrl = applicationContext.getEnvironment().resolvePlaceholders(url);

		driverHandler.goToUrl(pageUrl);
		AnnotationsSupport.initLocators(this);
	}

	protected AbstractPage() {
		this(null);
	}

	protected AbstractPage(AbstractPage parentPage, String url) {
		// TODO
		this(url);
	}

	public String getTitle() {
		return driverHandler.getTitle();
	}

	void onTestFail() {

		ISuite currentTestSuite = TestThreadPoolManager.getCurrentTestSuite();

		Long currentTime = null;

		// So that never would be two photos with same time stamp
		synchronized (currentTime) {
			currentTime = new Date().getTime();
		}

		String filePath = currentTestSuite.getOutputDirectory() + File.separator + "fail-photos" + File.separator + driverHandler.getBrowser() + File.separator + currentTime + ".png";

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

		String[] locations = { "classpath:/context/page-context.xml" };

		String[] contextLocations = getContextLocations();
		locations = ArrayUtils.addAll(locations, contextLocations);

		applicationContext = new GenericXmlApplicationContext(locations);

		Properties suiteProperties = TestThreadPoolManager.getSuitePropertiesForPage();
		SpringUtil.addProperties(applicationContext, suiteProperties);

		addPageProperties();

		SpringUtil.autowireBean(applicationContext, this);
		TestThreadPoolManager.registerPage(this);
	}

	private void addPageProperties() {

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

		Class<?> testClass = this.getClass();
		PageContext annotation = testClass.getAnnotation(PageContext.class);
		if (annotation != null) {
			contextLocations = annotation.value();
		}

		return contextLocations;
	}
}
