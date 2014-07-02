package com.mgiorda.testng;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({ TestRegister.class, TestEventDispatcher.class, TestLogger.class })
public abstract class AbstractTest {

	private static final Log staticLogger = LogFactory.getLog(AbstractTest.class);

	protected final Log logger = LogFactory.getLog(this.getClass());

	public AbstractTest() {

		CurrentTestRun.registerTestInstance(this);

		TestEventDispatcher eventDispatcher = TestEventDispatcher.getEventDispatcher();
		eventDispatcher.onClassStart(this);
	}

	@BeforeClass
	public void $beforeClassRegisterAndLog() {

		staticLogger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));
	}

	@AfterClass(alwaysRun = true)
	public void $afterClassUnregisterAndLog() {

		TestEventDispatcher eventDispatcher = TestEventDispatcher.getEventDispatcher(this);
		eventDispatcher.onClassFinish(this);

		staticLogger.info(String.format("Finishing test Class '%s'", this.getClass().getSimpleName()));
	}
}
