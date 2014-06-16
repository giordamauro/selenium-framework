package com.mgiorda.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Predicate;
import com.mgiorda.page.Browser;
import com.mgiorda.page.WebDriverFactory;

public abstract class AbstractPage {

	// DO NOT change visibility to public - Is protected to prevent use of
	// WebElements directly in tests
	protected static class PageElement {

		private static final Log logger = LogFactory.getLog(PageElement.class);

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

			String keys = "";
			for (CharSequence seq : keysToSend) {
				keys += seq.toString();
			}
			logger.info(String.format("PageElement(%s) - Sent keys '%s'", this.hashCode(), keys));

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

	private static final Log staticLogger = LogFactory.getLog(AbstractPage.class);

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private WebDriverFactory driverHandler;

	@Value("${test.waitTimeOut}")
	private int waitTimeOut;

	@Value("${test.browser}")
	private Browser browser;

	private final WebDriver driver;
	private final AbstractPage parentPage;

	protected AbstractPage(String url) {

		if (url == null) {
			throw new IllegalArgumentException("Url constructor parameter cannot be null");
		}

		this.parentPage = null;
		AbstractTest test = TestPoolManager.getCurrentTest();
		test.initPageContext(this);

		this.driver = driverHandler.getNewDriver(browser);

		goToUrl(url);
		AnnotationsSupport.initFindBy(this);
	}

	protected AbstractPage(AbstractPage parentPage, String url) {

		if (parentPage == null || url == null) {
			throw new IllegalArgumentException("ParentPage and url constructor parameters cannot be null");
		}

		this.parentPage = parentPage;

		// means not opening a new browser
		this.driver = parentPage.driver;
		this.waitTimeOut = parentPage.waitTimeOut;

	}

	void onTestFinish() {

		this.quit();
	}

	public void quit() {
		if (parentPage != null) {
			parentPage.quit();
		} else {
			if (!driver.toString().contains("(null)")) {
				driver.quit();
			}
		}
	}

	protected boolean existsElement(By elementLocator) {

		boolean exists = true;

		try {

			long start = new Date().getTime();

			new WebDriverWait(driver, waitTimeOutMillis()).until(ExpectedConditions.presenceOfElementLocated(elementLocator));

			long end = new Date().getTime();
			long waitTime = end - start;

			staticLogger.info(String.format("Found '%s' existent page element '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), elementLocator, waitTime));

		} catch (TimeoutException e) {
			exists = false;
		}

		return exists;

	}

	protected PageElement getElement(By elementLocator) {

		long start = new Date().getTime();

		WebElement element = new WebDriverWait(driver, waitTimeOutMillis()).until(ExpectedConditions.presenceOfElementLocated(elementLocator));

		PageElement pageElement = new PageElement(element);

		long end = new Date().getTime();
		long waitTime = end - start;

		staticLogger.info(String.format("Found '%s' page element '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), elementLocator, waitTime));

		return pageElement;
	}

	protected List<PageElement> getElements(By elementLocator) {

		long start = new Date().getTime();

		List<WebElement> elements = new WebDriverWait(driver, waitTimeOutMillis()).until(ExpectedConditions.presenceOfAllElementsLocatedBy(elementLocator));

		List<PageElement> pageElements = new ArrayList<>();

		for (WebElement element : elements) {
			PageElement pageElement = new PageElement(element);

			pageElements.add(pageElement);
		}

		long end = new Date().getTime();
		long waitTime = end - start;

		staticLogger.info(String.format("Found '%s' page elements '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), elementLocator, waitTime));

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

	private void goToUrl(String url) {

		driver.navigate().to(url);
		long loadTime = waitForPageToLoad();

		staticLogger.info(String.format("Navigated form page '%s' to url '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), url, loadTime));
	}

	private long waitForPageToLoad() {

		long start = new Date().getTime();

		new WebDriverWait(driver, waitTimeOutMillis()).until(new Predicate<WebDriver>() {

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

	private long waitTimeOutMillis() {
		return waitTimeOut * 1000;
	}

}
