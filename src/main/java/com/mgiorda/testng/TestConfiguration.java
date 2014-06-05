package com.mgiorda.testng;

import com.mgiorda.selenium.DriverPolicy;
import com.mgiorda.selenium.DriverPoolManager;

public class TestConfiguration {

	private final int waitTimeOut;

	private final DriverPoolManager driverPoolManager;

	private final DriverPolicy driverPolicy;

	public TestConfiguration(int waitTimeOut, DriverPoolManager driverPoolManager, DriverPolicy driverPolicy) {

		if (driverPoolManager == null || driverPolicy == null) {
			throw new IllegalArgumentException("DriverPoolManager and driverPolicy constructor parameters cannot be null");
		}

		this.waitTimeOut = waitTimeOut;
		this.driverPoolManager = driverPoolManager;
		this.driverPolicy = driverPolicy;
	}

	public int getWaitTimeOut() {
		return waitTimeOut;
	}

	public DriverPoolManager getDriverPoolManager() {
		return driverPoolManager;
	}

	public DriverPolicy getDriverPolicy() {
		return driverPolicy;
	}
}
