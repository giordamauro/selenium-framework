package com.mgiorda.main;

import java.util.HashMap;
import java.util.Map;

import com.mgiorda.selenium.Browser;
import com.mgiorda.selenium.BrowserFactory;
import com.mgiorda.selenium.WebDriverHandler;
import com.mgiorda.testng.TestConfiguration;
import com.mgiorda.testng.TestSuiteRunner;

public class Main {

	public static void main(String[] args) {

		int waitTimeOut = 40;
		Map<Browser, BrowserFactory> browserFactories = new HashMap<>();
		WebDriverHandler driverFactory = new WebDriverHandler(browserFactories, waitTimeOut);
		TestConfiguration testConfig = new TestConfiguration(driverFactory);
		testConfig.setBrowser(Browser.CHROME);
		testConfig.setWaitTimeOut(waitTimeOut);

		new TestSuiteRunner(testConfig);
	}
}
