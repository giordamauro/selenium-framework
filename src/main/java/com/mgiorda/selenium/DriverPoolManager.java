package com.mgiorda.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.WebDriver;

public class DriverPoolManager {

	private final WebDriverFactory driverFactory;

	private final Map<AbstractPage, WebDriver> inUse = new HashMap<>();

	private final List<WebDriver> available = new ArrayList<>();

	public DriverPoolManager(WebDriverFactory driverFactory) {
		this.driverFactory = driverFactory;
	}

	synchronized <T extends AbstractPage> WebDriver getDriver(T page) {

		WebDriver driver = inUse.get(page);
		if (driver == null) {

			if (!available.isEmpty()) {
				driver = available.remove(0);
			} else {
				// TODO: think about driverFactory
				driver = driverFactory.newDriver(Browser.CHROME);
				inUse.put(page, driver);
			}
		}

		return driver;
	}

	synchronized <T extends AbstractPage> void releaseDriver(T page) {

		WebDriver driver = inUse.get(page);
		if (driver == null) {
			throw new IllegalStateException(String.format("Page '%s' does not have any browser assigned", page.getClass().getSimpleName()));
		}
		available.add(driver);
		inUse.remove(page);
	}

	synchronized <T extends AbstractPage> void quitDriver(T page) {

		WebDriver driver = inUse.get(page);
		if (driver == null) {
			throw new IllegalStateException(String.format("Page '%s' does not have any browser assigned", page.getClass().getSimpleName()));
		}
		;
		inUse.remove(page);
		driver.quit();
	}

	void quitAllDrivers() {

		for (WebDriver driver : available) {
			available.remove(driver);
			driver.quit();
		}

		for (Entry<AbstractPage, WebDriver> driverEntry : inUse.entrySet()) {
			// TODO: log this particular case
			inUse.remove(driverEntry.getKey());
			driverEntry.getKey().quit();
		}
	}
}
