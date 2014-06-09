package com.mgiorda.testng;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import com.mgiorda.selenium.DriverPolicyManager;

@Listeners({ SuiteLogger.class, TestLogger.class })
@ContextConfiguration(locations = { "classpath:/testsContext.xml" })
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

	@BeforeClass
	public void logBeforeClass() {
		logger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));
	}

	@AfterClass
	public void logAfterClass() {

		DriverPolicyManager.quitClassDrivers(this.getClass());

		logger.info(String.format("Finishing test Class '%s'", this.getClass().getSimpleName()));
	}
}
