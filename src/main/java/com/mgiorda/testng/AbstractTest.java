package com.mgiorda.testng;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({ TestRegister.class, TestLogger.class })
public abstract class AbstractTest {

	private static final Log staticLogger = LogFactory.getLog(AbstractTest.class);

	protected final Log logger = LogFactory.getLog(this.getClass());

	@BeforeClass
	public void $beforeClassRegisterAndLog() {

		CurrentTestRun.registerTestInstance(this);
		staticLogger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));
	}

	@AfterClass(alwaysRun = true)
	public void $afterClassUnregisterAndLog() {

		CurrentTestRun.unRegisterTestInstance(this);
		staticLogger.info(String.format("Finishing test Class '%s'", this.getClass().getSimpleName()));
	}
}
