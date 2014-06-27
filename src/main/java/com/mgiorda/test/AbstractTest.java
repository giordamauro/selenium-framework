package com.mgiorda.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import com.mgiorda.annotation.TestContext;
import com.mgiorda.annotation.TestProperties;
import com.mgiorda.common.SpringUtil;

@Listeners({ SuiteLogger.class, TestLogger.class })
public abstract class AbstractTest {

	protected static final class PageAssert {

		private PageAssert() {

		}

		public static void assertTitle(AbstractPage page, String expectedTitle) {

			if (page == null || expectedTitle == null) {
				throw new IllegalArgumentException("Page and Expected title cannot be null");
			}

			String pageTitle = page.getTitle();

			if (!expectedTitle.equals(pageTitle)) {

				throw new AssertionError(String.format("Page '%s' title not equal - Expected '%s' but was: '%s'", page, expectedTitle, pageTitle));
			}
		}
	}

	protected static final class AssertUtil {

		private AssertUtil() {

		}

		public static void assertListContainsCaseInsensitive(List<String> values, String expectedValue) {

			if (values == null || expectedValue == null) {
				throw new IllegalArgumentException("Page and Expected title cannot be null");
			}

			boolean contains = false;
			int i = 0;
			while (i < values.size() && !contains) {
				String value = values.get(i);
				if (value.equalsIgnoreCase(expectedValue)) {
					contains = true;
				}
				i++;
			}
			if (!contains) {
				throw new AssertionError(String.format("Expected value '%s' not contained (ignoreCase) in list '%s'", expectedValue, values));
			}
		}
	}

	private static final Log staticLogger = LogFactory.getLog(AbstractPage.class);

	protected final Log logger = LogFactory.getLog(this.getClass());

	private ApplicationContext testAppContext = null;

	@BeforeClass
	public void $beforeClass() {
		staticLogger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));

		testAppContext = new GenericXmlApplicationContext(new String[] { "classpath:/context/test-context.xml" });
		setSuiteProperties();

		String[] contextLocations = getContextLocations();
		if (contextLocations.length != 0) {
			testAppContext = new ClassPathXmlApplicationContext(contextLocations, testAppContext);
		}

		addTestProperties();

		TestThreadPoolManager.registerTestInstance(this);
		SpringUtil.autowireBean(testAppContext, this);
	}

	@AfterClass
	public void $logAfterClass() {

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

			if (contextLocations.length == 0) {

				String path = testClass.getName().replaceAll("\\.", "/");
				String contextFile = "classpath*:" + path + "-context.xml";

				contextLocations = new String[] { contextFile };
			}
		}

		return contextLocations;
	}

	private void addTestProperties() {

		Class<?> testClass = this.getClass();

		InputStream resource = testClass.getResourceAsStream(testClass.getSimpleName() + ".properties");
		if (resource != null) {
			Properties properties = new Properties();
			try {
				properties.load(resource);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			SpringUtil.addProperties(testAppContext, properties);
		}

		TestProperties annotation = testClass.getAnnotation(TestProperties.class);
		if (annotation != null) {

			String[] values = annotation.value();

			for (String propertySource : values) {
				SpringUtil.addPropertiesFile(testAppContext, propertySource);
			}
		}
	}
}
