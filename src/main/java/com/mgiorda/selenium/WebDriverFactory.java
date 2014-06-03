package com.mgiorda.selenium;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverFactory {

	private static final Log logger = LogFactory.getLog(WebDriverFactory.class);

	// TODO configurable por Spring
	private final Browser browser;

	public WebDriverFactory(Browser browser) {
		this.browser = browser;
	}

	public WebDriver newDriver() {

		logger.info(String.format("-- Openning %s driver --", browser));

		System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");

		return new ChromeDriver();
	}
}
