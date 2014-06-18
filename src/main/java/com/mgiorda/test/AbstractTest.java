package com.mgiorda.test;

import java.util.Properties;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import com.mgiorda.commons.SpringUtil;
import com.mgiorda.page.PageProperties;

@Listeners({ SuiteLogger.class, TestLogger.class })
@ContextConfiguration("classpath:/context/test-context.xml")
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

	@BeforeClass
	public void beforeClass() {
		logger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));

		Class<?> testClass = this.getClass();
		TestProperties annotation = testClass.getAnnotation(TestProperties.class);
		if (annotation != null) {

			String[] values = annotation.value();

			for (String propertySource : values) {
				SpringUtil.addPropetiesFile(applicationContext, propertySource);
			}

			SpringUtil.autowireBean(applicationContext, this);
		}
	}

	@AfterClass
	public void logAfterClass() {

		logger.info(String.format("Finishing test Class '%s'", this.getClass().getSimpleName()));
	}

	<T extends AbstractPage> void initPageContext(T page) {

		Class<?> pageClass = page.getClass();
		PageProperties annotation = pageClass.getAnnotation(PageProperties.class);
		if (annotation != null) {

			String[] values = annotation.value();

			for (String propertySource : values) {
				SpringUtil.addPropetiesFile(applicationContext, propertySource);
			}
		}

		Properties properties = TestThreadPoolManager.getSuiteProperties();
		if (properties != null) {
			SpringUtil.addProperties(applicationContext, properties);
		}

		SpringUtil.autowireBean(applicationContext, page);
		TestThreadPoolManager.registerPage(page);
	}
}