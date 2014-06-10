package com.mgiorda.main;

import java.util.HashMap;
import java.util.Map;

import com.mgiorda.testng.Browser;
import com.mgiorda.testng.BrowserFactory;
import com.mgiorda.testng.DriverPolicy;
import com.mgiorda.testng.TestConfiguration;
import com.mgiorda.testng.TestSuiteRunner;
import com.mgiorda.testng.WebDriverHandler;

public class Main {

	public static void main(String[] args) {

		int waitTimeOut = 40;
		Map<Browser, BrowserFactory> browserFactories = new HashMap<>();
		WebDriverHandler driverFactory = new WebDriverHandler(browserFactories, waitTimeOut);
		TestConfiguration testConfig = new TestConfiguration(driverFactory);
		testConfig.setBrowser(Browser.CHROME);
		testConfig.setDriverPolicy(DriverPolicy.FINISH_PER_SUITE);
		testConfig.setWaitTimeOut(waitTimeOut);

		new TestSuiteRunner(testConfig);
	}
}
