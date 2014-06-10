package com.mgiorda.testng;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({ SuiteLogger.class, TestLogger.class })
@ContextConfiguration(locations = { "classpath:/testsContext.xml" })
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

	@BeforeClass
	public void logBeforeClass() {
		logger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));
	}

	<T extends AbstractPage> void initPageContext(T page) {

		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(page, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
		beanFactory.initializeBean(page, page.getClass().getName());

		TestPoolManager.registerPage(page);
	}

	@AfterClass
	public void logAfterClass() {

		logger.info(String.format("Finishing test Class '%s'", this.getClass().getSimpleName()));
	}
}
