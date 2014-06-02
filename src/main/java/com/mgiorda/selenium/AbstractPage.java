package com.mgiorda.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.mgiorda.testng.PerThreadSuiteConfig;
import com.mgiorda.testng.SuiteConfiguration;

public abstract class AbstractPage {

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

	private static final WebDriverFactory driverFactory = new WebDriverFactory();

	private final WebDriver driver;
	private final SuiteConfiguration suiteConfig;
	private final String relativeUrl;

	protected AbstractPage(String relativeUrl) {

		// Values coming from configuration
		this.suiteConfig = PerThreadSuiteConfig.getConfiguration();
		Browser browser = suiteConfig.getBrowser();

		this.driver = driverFactory.newDriver(browser);
		this.relativeUrl = relativeUrl;

		goToUrl(relativeUrl);
		AnnotationsSupport.initFindBy(this);
	}

	protected AbstractPage(AbstractPage page, String relativeUrl) {

		// TODO: update to the other constructor

		// means not opening a new browser
		this.driver = page.driver;
		this.suiteConfig = page.suiteConfig;
		this.relativeUrl = relativeUrl;
	}

	public void quit() {
		driver.quit();
	}

	protected void goToUrl(String relativeUrl) {

		driver.navigate().to(suiteConfig.getHost() + relativeUrl);
		waitForPageToLoad();
	}

	protected PageElement getElement(By elementLocator) {

		long waitTimeOut = suiteConfig.getWaitTimeOut() * 1000;
		WebElement element = new WebDriverWait(driver, waitTimeOut).until(ExpectedConditions.presenceOfElementLocated(elementLocator));

		PageElement pageElement = new PageElement(element);

		return pageElement;
	}

	protected List<PageElement> getElements(By elementLocator) {

		long waitTimeOut = suiteConfig.getWaitTimeOut() * 1000;
		List<WebElement> elements = new WebDriverWait(driver, waitTimeOut).until(ExpectedConditions.presenceOfAllElementsLocatedBy(elementLocator));

		List<PageElement> pageElements = new ArrayList<>();

		for (WebElement element : elements) {
			PageElement pageElement = new PageElement(element);

			pageElements.add(pageElement);
		}

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

	protected String getHost() {
		return suiteConfig.getHost();
	}

	protected String getRelativeUrl() {
		return relativeUrl;
	}

	private void waitForPageToLoad() {

		long waitTimeOut = suiteConfig.getWaitTimeOut() * 1000;
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
	}

}
