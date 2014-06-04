package com.mgiorda.testng;

import com.mgiorda.selenium.DriverPoolManager;

public class TestConfiguration {

	private final int waitTimeOut;

	private final DriverPoolManager driverPoolManager;

	public TestConfiguration(int waitTimeOut, DriverPoolManager driverPoolManager) {

		if (driverPoolManager == null) {
			throw new IllegalArgumentException("DriverPoolManager constructor parameter cannot be null");
		}

		this.waitTimeOut = waitTimeOut;
		this.driverPoolManager = driverPoolManager;
	}

	public int getWaitTimeOut() {
		return waitTimeOut;
	}

	public DriverPoolManager getDriverPoolManager() {
		return driverPoolManager;
	}
}
