package com.mgiorda.page;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

public class DriverActionHandlerImpl implements DriverActionHandler {

	private static final Log logger = LogFactory.getLog(AbstractPage.class);

	private final WebDriver driver;
	private final WebDriverWait driverWait;

	public DriverActionHandlerImpl(WebDriver driver, WebDriverWait webDriverWait) {

		if (driver == null) {
			throw new IllegalArgumentException("Driver cannot be null");
		}

		this.driver = driver;
		this.driverWait = webDriverWait;
	}

	public String getTitle() {
		return driver.getTitle();
	}

	public String getUrl() {
		return driver.getCurrentUrl();
	}

	public void quit() {

		if (!driver.toString().contains("(null)")) {

			logger.info(String.format("Quitting driver '%s'", driver));
			driver.quit();
		}
	}

	public void goToUrl(String url) {

		driver.navigate().to(url);

		if (driverWait != null) {
			waitForPageToLoad();
		}

		logger.info(String.format("Browsed '%s' to url '%s'", driver, url));
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

		logger.info(String.format("Waited for page to laod %s milliseconds", waitTime));
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
}
