package com.mgiorda.testng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({ SuiteLogger.class, TestLogger.class })
@ContextConfiguration(locations = { "classpath:/testsContext.xml" })
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private TestConfiguration testConfig;

	@BeforeClass
	public void logBeforeClass() {
		logger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));
	}

	<T extends AbstractPage> void initPageContext(T page) {

		@SuppressWarnings("resource")
		AutowireCapableBeanFactory beanFactory = new GenericApplicationContext().getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(page, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
		beanFactory.initializeBean(page, page.getClass().getName());
	}

	@AfterClass
	public void logAfterClass() {

		DriverPolicyManager.quitClassDrivers(this.getClass());

		logger.info(String.format("Finishing test Class '%s'", this.getClass().getSimpleName()));
	}
}
