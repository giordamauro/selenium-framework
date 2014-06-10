package com.mgiorda.testng;


public class TestConfiguration {

	private final WebDriverHandler driverHandler;

	private int waitTimeOut;

	private Browser browser;

	private DriverPolicy driverPolicy;

	public TestConfiguration(WebDriverHandler driverHandler) {

		if (driverHandler == null) {
			throw new IllegalArgumentException("DriverHandler constructor parameter cannot be null");
		}

		this.driverHandler = driverHandler;
	}

	public int getWaitTimeOut() {
		return waitTimeOut;
	}

	public DriverPolicy getDriverPolicy() {
		return driverPolicy;
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public WebDriverHandler getDriverHandler() {
		return driverHandler;
	}

	public void setWaitTimeOut(int waitTimeOut) {
		this.waitTimeOut = waitTimeOut;
	}

	public void setDriverPolicy(DriverPolicy driverPolicy) {
		this.driverPolicy = driverPolicy;
	}

}
