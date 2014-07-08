package com.mgiorda.page.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.mgiorda.page.DriverActionHandler;

public class DriverActionHandlerImpl implements DriverActionHandler {

	private static final Log logger = LogFactory.getLog(DriverActionHandlerImpl.class);

	private final WebDriver driver;
	private final WebDriverWait driverWait;

	public DriverActionHandlerImpl(WebDriver driver, WebDriverWait driverWait, long timeOutInSeconds) {

		if (driver == null) {
			throw new IllegalArgumentException("Driver cannot be null");
		}

		this.driver = driver;
		this.driverWait = driverWait;

		driver.manage().timeouts().pageLoadTimeout(timeOutInSeconds, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(timeOutInSeconds, TimeUnit.SECONDS);

		driver.manage().window().maximize();
	}

	public String getTitle() {
		return driver.getTitle();
	}

	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	public void quit() {

		if (!driver.toString().contains("(null)")) {

			logger.info(String.format("Quitting driver '%s'", driver));
			driver.quit();
		}
	}

	public void goToUrl(String url) {

		logger.debug(String.format("Browsing to url '%s' - Driver: '%s'", url, driver));

		driver.navigate().to(url);

		if (driverWait != null) {
			waitForPageToLoad();
		}
	}

	public void waitForPageToLoad() throws TimeoutException {

		long start = new Date().getTime();

		Predicate<WebDriver> stateReady = new Predicate<WebDriver>() {

			@Override
			public boolean apply(WebDriver driver) {

				boolean apply = false;

				JavascriptExecutor js = (JavascriptExecutor) driver;
				Object obj = js.executeScript("return document.readyState");

				if (obj != null) {
					String value = String.valueOf(obj);

					apply = (value.equals("complete"));
				}

				return apply;
			}
		};

		driverWait.until(stateReady);

		long end = new Date().getTime();
		long waitTime = end - start;

		logger.debug(String.format("Load page after %s milliseconds", waitTime));
	}

	public void takeScreenShot(String filePath) {

		TakesScreenshot screenShotDriver = null;
		try {
			screenShotDriver = (TakesScreenshot) driver;
		} catch (Exception e) {
			logger.warn(String.format("Driver '%s' cannot take screenshots", driver));
		}

		if (screenShotDriver != null) {
			File screenShot = screenShotDriver.getScreenshotAs(OutputType.FILE);

			try {
				logger.info(String.format("Saving screenshot to file '%s'", filePath));

				FileUtils.copyFile(screenShot, new File(filePath));
			} catch (IOException e) {

				throw new IllegalStateException(String.format("Exception trying to save screenshot to file '%s'", filePath), e);
			}
		}
	}

	WebDriver getDriver() {
		return driver;
	}

	WebDriverWait getDriverWait() {
		return driverWait;
	}

	@Override
	public String getDriverName() {

		Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
		String driverName = capabilities.getBrowserName();

		return driverName;
	}
}
