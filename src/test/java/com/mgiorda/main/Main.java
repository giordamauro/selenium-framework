package com.mgiorda.main;

import com.mgiorda.selenium.Browser;
import com.mgiorda.testng.SuiteConfiguration;
import com.mgiorda.testng.TestSuiteRunner;

public class Main {

	public static void main(String[] args) {

		SuiteConfiguration suiteConfig = new SuiteConfiguration();
		suiteConfig.setBrowser(Browser.CHROME);
		suiteConfig.setHost("http://www.google.com");

		new TestSuiteRunner(suiteConfig);
	}
}
