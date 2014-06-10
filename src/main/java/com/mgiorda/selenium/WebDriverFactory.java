package com.mgiorda.selenium;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;

public class WebDriverFactory {

	private static final Log logger = LogFactory.getLog(WebDriverFactory.class);

	private final Map<Browser, BrowserFactory> browserFactories;

	private final int timeOutSeconds;

	public WebDriverFactory(Map<Browser, BrowserFactory> browserFactories, int timeOutSeconds) {
		if (browserFactories == null || timeOutSeconds == 0) {
			throw new IllegalArgumentException("BrowserFactories constructor parameter cannot be null and timeOutSeconds cannot be 0");
		}

		this.browserFactories = browserFactories;
		this.timeOutSeconds = timeOutSeconds;
	}

	public WebDriver getNewDriver(Browser browser) {

		BrowserFactory factory = browserFactories.get(browser);
		if (factory == null) {
			throw new IllegalStateException(String.format("There isn't any BrowserFactory registered for browser %s", browser));
		}

		logger.info(String.format("Openning %s driver", browser));
		WebDriver driver = factory.newDriver();

		driver.manage().timeouts().pageLoadTimeout(timeOutSeconds, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(timeOutSeconds, TimeUnit.SECONDS);

		return driver;
	}
}
