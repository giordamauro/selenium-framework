package com.mgiorda.main;

import com.mgiorda.selenium.Browser;
import com.mgiorda.selenium.DriverPoolManager;
import com.mgiorda.selenium.WebDriverFactory;
import com.mgiorda.testng.TestConfiguration;
import com.mgiorda.testng.TestSuiteRunner;

public class Main {

	public static void main(String[] args) {

		int waitTimeOut = 40;
		WebDriverFactory driverFactory = new WebDriverFactory(Browser.CHROME);
		DriverPoolManager driverPoolManager = new DriverPoolManager(driverFactory);
		TestConfiguration testConfig = new TestConfiguration(waitTimeOut, driverPoolManager);

		new TestSuiteRunner(testConfig);
	}
}
