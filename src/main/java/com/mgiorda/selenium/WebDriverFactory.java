package com.mgiorda.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverFactory {

	// TODO configurable por Spring

	public WebDriver newDriver(Browser browser) {
		System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
		return new ChromeDriver();
	}
}
