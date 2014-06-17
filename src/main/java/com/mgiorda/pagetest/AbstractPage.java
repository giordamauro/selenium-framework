package com.mgiorda.pagetest;

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

	protected static class PageElement {

		private static final Log logger = LogFactory.getLog(PageElement.class);

		private final WebElement element;

		private PageElement(WebElement element) {
			this.element = element;
		}

		public void click() {
			logger.info(String.format("PageElement(%s) - Clicking", this.hashCode()));

			element.click();
		}

		public void submit() {
			logger.info(String.format("PageElement(%s) - Submitting", this.hashCode()));

			element.submit();
		}

		public void sendKeys(CharSequence... keysToSend) {

			String keys = "";
			for (CharSequence seq : keysToSend) {
				keys += seq.toString();
			}
			logger.info(String.format("PageElement(%s) - Sending keys '%s'", this.hashCode(), keys));

			element.sendKeys(keysToSend);
		}

		public void clear() {
			logger.info(String.format("PageElement(%s) - Clearing", this.hashCode()));

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

	protected static final class Locator {

		private final By by;

		private Locator(By by) {
			this.by = by;
		}

		public String toString() {
			return by.toString();
		}

		public static Locator byId(String id) {
			return new Locator(By.id(id));
		}

		public static Locator byLinkText(String linkText) {
			return new Locator(By.linkText(linkText));
		}

		public static Locator byPartialLinkText(String partialLinkText) {
			return new Locator(By.partialLinkText(partialLinkText));
		}

		public static Locator byName(String name) {
			return new Locator(By.name(name));
		}

		public static Locator byTagName(String tagName) {
			return new Locator(By.tagName(tagName));
		}

		public static Locator byXpath(String xpath) {
			return new Locator(By.xpath(xpath));
		}

		public static Locator byClass(String className) {
			return new Locator(By.className(className));
		}

		public static Locator byCssSelector(String id) {
			return new Locator(By.id(id));
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

	protected boolean existsElement(Locator elementLocator) {

		boolean exists = true;

		try {

			long start = new Date().getTime();

			new WebDriverWait(driver, waitTimeOutMillis()).until(ExpectedConditions.presenceOfElementLocated(elementLocator.by));

			long end = new Date().getTime();
			long waitTime = end - start;

			staticLogger.info(String.format("Found '%s' existent page element '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), elementLocator, waitTime));

		} catch (TimeoutException e) {
			exists = false;
		}

		return exists;

	}

	protected PageElement getElement(Locator elementLocator) {

		long start = new Date().getTime();

		WebElement element = new WebDriverWait(driver, waitTimeOutMillis()).until(ExpectedConditions.presenceOfElementLocated(elementLocator.by));

		PageElement pageElement = new PageElement(element);

		long end = new Date().getTime();
		long waitTime = end - start;

		staticLogger.info(String.format("Found '%s' page element '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), elementLocator, waitTime));

		return pageElement;
	}

	protected List<PageElement> getElements(Locator elementLocator) {

		long start = new Date().getTime();

		List<WebElement> elements = new WebDriverWait(driver, waitTimeOutMillis()).until(ExpectedConditions.presenceOfAllElementsLocatedBy(elementLocator.by));

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

	protected PageElement getSubElement(PageElement pageElement, Locator elementLocator) {
		// TODO
		return null;
	}

	protected List<PageElement> getSubElements(PageElement pageElement, Locator elementLocator) {
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