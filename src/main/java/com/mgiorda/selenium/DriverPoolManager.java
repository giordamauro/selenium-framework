package com.mgiorda.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;

public class DriverPoolManager {

	private final static Log logger = LogFactory.getLog(DriverPoolManager.class);

	private final WebDriverFactory driverFactory;

	private final Map<AbstractPage, WebDriver> inUse = new HashMap<>();

	private final List<WebDriver> available = new ArrayList<>();

	public DriverPoolManager(WebDriverFactory driverFactory) {

		if (driverFactory == null) {
			throw new IllegalArgumentException("WebDriverFactory constructor parameter cannot be null");
		}

		this.driverFactory = driverFactory;
	}

	synchronized <T extends AbstractPage> WebDriver getDriver(T page) {

		WebDriver driver = inUse.get(page);
		if (driver == null) {

			if (!available.isEmpty()) {
				driver = available.remove(0);
			} else {
				driver = driverFactory.newDriver();
				inUse.put(page, driver);

				logger.info(String.format("Assigned driver %s to page %s", driver.toString(), page.getClass().getSimpleName()));
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

		logger.info(String.format("Releaseed driver %s", driver.toString()));
	}

	synchronized <T extends AbstractPage> void quitDriver(T page) {

		WebDriver driver = inUse.get(page);
		if (driver == null) {
			throw new IllegalStateException(String.format("Page '%s' does not have any browser assigned", page.getClass().getSimpleName()));
		}
		;
		inUse.remove(page);
		logger.info(String.format("Quitting driver %s", driver.toString()));

		driver.quit();
	}

	void quitAllDrivers() {

		for (WebDriver driver : available) {

			logger.info(String.format("Quitting available driver %s", driver.toString()));

			available.remove(driver);
			driver.quit();
		}

		for (Entry<AbstractPage, WebDriver> driverEntry : inUse.entrySet()) {

			AbstractPage page = driverEntry.getKey();
			WebDriver driver = driverEntry.getValue();

			logger.warn(String.format("Quitting driver %s assigned to page %s", driver.toString(), page.getClass().getSimpleName()));

			inUse.remove(page);
			driver.quit();
		}
	}
}
