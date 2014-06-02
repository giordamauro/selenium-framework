package com.mgiorda.testng;

import com.mgiorda.selenium.Browser;

public class SuiteConfiguration {

	private Browser browser = Browser.CHROME;

	private String host = "http://www.google.com";

	// in seconds
	private int waitTimeOut = 30;

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getWaitTimeOut() {
		return waitTimeOut;
	}

	public void setWaitTimeOut(int waitTimeOut) {
		this.waitTimeOut = waitTimeOut;
	}
}
