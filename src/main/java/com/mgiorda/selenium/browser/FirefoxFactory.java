package com.mgiorda.selenium.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.mgiorda.selenium.BrowserFactory;

public class FirefoxFactory implements BrowserFactory {

	@Override
	public WebDriver newDriver() {
		DesiredCapabilities firefoxCapabilities = DesiredCapabilities.firefox();
		WebDriver driver = new FirefoxDriver(firefoxCapabilities);

		return driver;
	}

}
