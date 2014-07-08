package com.mgiorda.testng;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({ TestRegister.class, TestLogger.class })
public abstract class AbstractTest {

	protected final Log logger = LogFactory.getLog(this.getClass());

	@BeforeClass
	public void $beforeClassRegisterAndLog() {

		logger.info(">>-- Initiating test Class");

		CurrentTestRun.registerTestInstance(this);

		TestEventDispatcher eventDispatcher = TestEventDispatcher.getEventDispatcher(this);
		eventDispatcher.onClassStart(this);
	}

	@AfterClass(alwaysRun = true)
	public void $afterClassLog() {

		TestEventDispatcher eventDispatcher = TestEventDispatcher.getEventDispatcher(this);
		eventDispatcher.onClassFinish(this);

		logger.info("<<-- Finishing test Class");
	}
}
