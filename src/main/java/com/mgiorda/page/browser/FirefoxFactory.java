package com.mgiorda.page.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.FactoryBean;

public class FirefoxFactory implements BrowserFactory, FactoryBean<WebDriver> {

	@Override
	public WebDriver newDriver() {
		DesiredCapabilities firefoxCapabilities = DesiredCapabilities.firefox();
		WebDriver driver = new FirefoxDriver(firefoxCapabilities);

		return driver;
	}

	@Override
	public WebDriver getObject() throws Exception {
		return newDriver();
	}

	@Override
	public Class<?> getObjectType() {
		return WebDriver.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
