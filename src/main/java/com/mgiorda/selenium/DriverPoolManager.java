package com.mgiorda.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;

import com.mgiorda.testng.TestPoolManager;

public class DriverPoolManager {

	private final static Log logger = LogFactory.getLog(DriverPoolManager.class);

	private final static Map<ISuite, DriverPoolManager> suiteDriverManagers = new HashMap<>();

	private final Map<AbstractPage, WebDriver> inUse = new HashMap<>();
	private final List<WebDriver> available = new ArrayList<>();

	private final WebDriverHandler driverFactory;
	private Browser browser;

	private DriverPoolManager(WebDriverHandler driverFactory, Browser browser) {

		if (driverFactory == null || browser == null) {
			throw new IllegalArgumentException("WebDriverFactory and browser constructor parameters cannot be null");
		}

		this.driverFactory = driverFactory;
		this.browser = browser;
	}

	synchronized <T extends AbstractPage> WebDriver getDriver(T page) {

		WebDriver driver = inUse.get(page);
		if (driver == null) {

			if (!available.isEmpty()) {
				driver = available.remove(0);
			} else {
				driver = driverFactory.getNewDriver(browser);
				inUse.put(page, driver);

				logger.info(String.format("Assigned driver %s to page %s", driver.toString(), page.getClass().getSimpleName()));
			}
		}

		TestPoolManager.registerTestPage(page);

		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();

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

	public void setBrowser(Browser browser) {

		if (browser == null) {
			throw new IllegalArgumentException("Browser constructor parameter cannot be null");
		}
		this.browser = browser;
	}

	public synchronized void quitAllDrivers() {

		for (WebDriver driver : available) {

			logger.info(String.format("Quitting available driver %s", driver.toString()));

			driver.quit();
		}
		available.clear();

		for (Entry<AbstractPage, WebDriver> driverEntry : inUse.entrySet()) {

			AbstractPage page = driverEntry.getKey();
			WebDriver driver = driverEntry.getValue();

			logger.warn(String.format("Quitting driver %s assigned to page %s", driver.toString(), page.getClass().getSimpleName()));

			driver.quit();
		}
		inUse.clear();
	}

	public static void registerSuiteDriverManager(WebDriverHandler driverFactory, Browser browser, ISuite suite) {

		DriverPoolManager driverManager = new DriverPoolManager(driverFactory, browser);
		suiteDriverManagers.put(suite, driverManager);
	}

	public static DriverPoolManager getDriverManager() {

		ISuite suite = TestPoolManager.getCurrentTestSuite();

		return getDriverManagerForSuite(suite);
	}

	public static DriverPoolManager getDriverManagerForSuite(ISuite suite) {

		DriverPoolManager driverManager = suiteDriverManagers.get(suite);

		if (driverManager == null) {
			throw new IllegalStateException(String.format("Exception getting DriverManager for suite named %s", suite.getName()));
		}

		return driverManager;
	}
}
