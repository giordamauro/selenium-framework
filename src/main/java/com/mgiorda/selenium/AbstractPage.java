package com.mgiorda.selenium;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.mgiorda.testng.PerThreadTestConfig;
import com.mgiorda.testng.TestConfiguration;

public abstract class AbstractPage {

	private static final Log logger = LogFactory.getLog(AbstractPage.class);

	// DO NOT change visibility to public - Is protected to prevent use of
	// WebElements directly in tests
	protected static class PageElement {

		private final WebElement element;

		private PageElement(WebElement element) {
			this.element = element;
		}

		public void click() {
			element.click();
		}

		public void submit() {
			element.submit();
		}

		public void sendKeys(CharSequence... keysToSend) {
			element.sendKeys(keysToSend);
		}

		public void clear() {
			element.clear();
		}

		public String getTagName() {
			return element.getTagName();
		}

		public String getAttribute(String name) {
			return element.getAttribute(name);
		}

		public boolean isSelected() {
			return element.isSelected();
		}

		public boolean isEnabled() {
			return element.isEnabled();
		}

		public String getText() {
			return element.getText();
		}

		public boolean isDisplayed() {
			return element.isDisplayed();
		}

		public Point getLocation() {
			return element.getLocation();
		}

		public Dimension getSize() {
			return element.getSize();
		}

		public String getCssValue(String propertyName) {
			return element.getCssValue(propertyName);
		}
	}

	private final WebDriver driver;
	private final AbstractPage parentPage;
	private final DriverPoolManager driverPoolManager;
	private final long waitTimeOut;

	protected AbstractPage(String url) {

		this.parentPage = null;

		// Values coming from configuration
		TestConfiguration testConfig = PerThreadTestConfig.getConfiguration();

		this.waitTimeOut = testConfig.getWaitTimeOut() * 1000;
		this.driverPoolManager = testConfig.getDriverPoolManager();

		this.driver = driverPoolManager.getDriver(this);

		goToUrl(url);
		AnnotationsSupport.initFindBy(this);
	}

	protected AbstractPage(AbstractPage parentPage, String url) {

		this.parentPage = parentPage;
		// TODO: update to the other constructor, como manejar SubPages..

		// means not opening a new browser
		this.driver = parentPage.driver;
		this.driverPoolManager = null;
		this.waitTimeOut = parentPage.waitTimeOut;

	}

	protected void releaseBrowser() {
		if (parentPage != null) {
			parentPage.releaseBrowser();
		} else {
			driverPoolManager.releaseDriver(this);
		}
	}

	public void quit() {
		if (parentPage != null) {
			parentPage.quit();
		} else {
			driverPoolManager.quitDriver(this);
		}
	}

	private void goToUrl(String url) {

		driver.navigate().to(url);
		long loadTime = waitForPageToLoad();

		logger.info(String.format("Navigated to url '%s' - Waited %s milliseconds", url, loadTime));
	}

	protected PageElement getElement(By elementLocator) {

		long start = new Date().getTime();

		WebElement element = new WebDriverWait(driver, waitTimeOut).until(ExpectedConditions.presenceOfElementLocated(elementLocator));

		PageElement pageElement = new PageElement(element);

		long end = new Date().getTime();
		long waitTime = end - start;

		logger.info(String.format("Found '%s' page element '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), elementLocator, waitTime));

		return pageElement;
	}

	protected List<PageElement> getElements(By elementLocator) {

		long start = new Date().getTime();

		List<WebElement> elements = new WebDriverWait(driver, waitTimeOut).until(ExpectedConditions.presenceOfAllElementsLocatedBy(elementLocator));

		List<PageElement> pageElements = new ArrayList<>();

		for (WebElement element : elements) {
			PageElement pageElement = new PageElement(element);

			pageElements.add(pageElement);
		}

		long end = new Date().getTime();
		long waitTime = end - start;

		logger.info(String.format("Found '%s' page elements '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), elementLocator, waitTime));

		return pageElements;
	}

	protected PageElement getSubElement(PageElement pageElement, By elementLocator) {
		// TODO
		return null;
	}

	protected List<PageElement> getSubElements(PageElement pageElement, By elementLocator) {
		// TODO
		return null;
	}

	private long waitForPageToLoad() {

		long start = new Date().getTime();

		new WebDriverWait(driver, waitTimeOut).until(new Predicate<WebDriver>() {

			@Override
			public boolean apply(WebDriver driver) {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				Object obj = js.executeScript("return document.readyState");
				if (obj == null) {
					return false;
				}
				String str = (String) obj;
				if (str.equals("complete")) {
					return true;
				}
				return false;
			}
		});

		long end = new Date().getTime();
		long waitTime = end - start;

		return waitTime;
	}

}
