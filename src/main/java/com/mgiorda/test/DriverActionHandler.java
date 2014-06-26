package com.mgiorda.test;

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

public class DriverActionHandler {

	private static final Log staticLogger = LogFactory.getLog(AbstractPage.class);

	private final WebDriverWait driverWait;

	private final WebDriver driver;

	public DriverActionHandler(WebDriverWait webDriverWait, WebDriver driver) {

		this.driverWait = webDriverWait;
		this.driver = driver;
	}

	public String getTitle() {
		return driver.getTitle();
	}

	public String getUrl() {
		return driver.getCurrentUrl();
	}

	public void quit() {

		if (!driver.toString().contains("(null)")) {

			staticLogger.info(String.format("Quitting driver '%s'", driver));
			driver.quit();
		}
	}

	public void goToUrl(AbstractPage page, String url) {

		driver.navigate().to(url);

		this.waitForPageToLoad(page);

		staticLogger.info(String.format("Navigated form page '%s' to url '%s'", page.getClass(), url));
	}

	void waitForPageToLoad(AbstractPage page) throws TimeoutException {

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

		staticLogger.info(String.format("Waited for page '%s' to laod - Waited %s milliseconds", page.getClass(), waitTime));
	}

	public void takeScreenShot(String filePath) {

		TakesScreenshot screenShotDriver = null;
		try {
			screenShotDriver = (TakesScreenshot) driver;
		} catch (Exception e) {
			staticLogger.warn(String.format("Driver '%s' cannot take screenshots", driver));
		}

		if (screenShotDriver != null) {
			File screenShot = screenShotDriver.getScreenshotAs(OutputType.FILE);

			try {
				staticLogger.info(String.format("Saving screenshot to file '%s'", filePath));

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
