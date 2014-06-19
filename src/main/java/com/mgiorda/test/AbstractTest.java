package com.mgiorda.test;

import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import com.mgiorda.annotations.TestContext;
import com.mgiorda.annotations.TestProperties;
import com.mgiorda.commons.SpringUtil;

@Listeners({ SuiteLogger.class, TestLogger.class })
public abstract class AbstractTest {

	private static final Log staticLogger = LogFactory.getLog(AbstractPage.class);

	protected final Log logger = LogFactory.getLog(this.getClass());

	private ApplicationContext testAppContext = null;

	@BeforeClass
	public void beforeClass() {
		staticLogger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));

		String[] locations = { "classpath:/context/test-context.xml" };

		String[] contextLocations = getContextLocations();
		locations = ArrayUtils.addAll(locations, contextLocations);

		testAppContext = new GenericXmlApplicationContext(locations);

		setSuiteProperties();
		addTestProperties();

		SpringUtil.autowireBean(testAppContext, this);
	}

	@AfterClass
	public void logAfterClass() {

		staticLogger.info(String.format("Finishing test Class '%s'", this.getClass().getSimpleName()));
	}

	private void setSuiteProperties() {

		Properties suiteProperties = TestThreadPoolManager.getSuiteProperties();

		if (suiteProperties == null) {
			suiteProperties = testAppContext.getBean("defaultSuiteProperties", Properties.class);
			TestThreadPoolManager.registerSuiteProperties(suiteProperties);
		}

		SpringUtil.addProperties(testAppContext, suiteProperties);
	}

	private String[] getContextLocations() {

		String[] contextLocations = {};

		Class<?> testClass = this.getClass();
		TestContext annotation = testClass.getAnnotation(TestContext.class);
		if (annotation != null) {
			contextLocations = annotation.value();
		}

		return contextLocations;
	}

	private void addTestProperties() {

		Class<?> testClass = this.getClass();
		TestProperties annotation = testClass.getAnnotation(TestProperties.class);
		if (annotation != null) {

			String[] values = annotation.value();

			for (String propertySource : values) {
				SpringUtil.addPropertiesFile(testAppContext, propertySource);
			}
		}
	}
}
