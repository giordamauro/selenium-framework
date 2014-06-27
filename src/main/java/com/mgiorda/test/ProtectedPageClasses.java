package com.mgiorda.test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;

abstract class ProtectedPageClasses {

	protected static final class PageElement {

		private static final Log logger = LogFactory.getLog(PageElement.class);

		private final WebElement element;
		private final long afterActionTime;

		private AbstractElement abstractElement;

		PageElement(WebElement element, long afterActionTime) {
			this.element = element;
			this.afterActionTime = afterActionTime;
		}

		WebElement getWebElement() {
			return element;
		}

		void setAbstractElement(AbstractElement abstractElement) {
			this.abstractElement = abstractElement;
		}

		public void click() {
			this.logAction("Clicking");

			element.click();
			waitForActionTime();
		}

		public void submit() {
			this.logAction("Submitting");

			element.submit();
			waitForActionTime();
		}

		public void sendKeys(CharSequence... keysToSend) {

			String keys = "";
			for (CharSequence seq : keysToSend) {
				keys += seq.toString();
			}
			this.logAction(String.format("Sending keys '%s'", keys));

			element.sendKeys(keysToSend);
		}

		public void clear() {
			this.logAction("Clearing");

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

		private void waitForActionTime() {
			try {
				Thread.sleep(afterActionTime);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private void logAction(String action) {

			if (abstractElement == null) {
				logger.info(String.format("PageElement(%s) - %s", element, action));
			} else {
				logger.info(String.format("%s(%s) - %s", abstractElement.getClass(), element, action));
			}
		}
	}

	protected static final class Locator {

		private final Class<? extends By> byClass;

		private final String value;

		private Locator(Class<? extends By> byClass, String value) {

			if (value == null) {
				throw new IllegalArgumentException("Locator value cannot be null");
			}
			this.byClass = byClass;
			this.value = value;
		}

		Class<? extends By> getByClass() {
			return byClass;
		}

		String getValue() {
			return value;
		}

		public static Locator byId(String id) {
			return new Locator(By.ById.class, id);
		}

		public static Locator byLinkText(String linkText) {
			return new Locator(By.ByLinkText.class, linkText);
		}

		public static Locator byPartialLinkText(String partialLinkText) {
			return new Locator(By.ByPartialLinkText.class, partialLinkText);
		}

		public static Locator byName(String name) {
			return new Locator(By.ByName.class, name);
		}

		public static Locator byTagName(String tagName) {
			return new Locator(By.ByTagName.class, tagName);
		}

		public static Locator byXpath(String xpath) {
			return new Locator(By.ByXPath.class, xpath);
		}

		public static Locator byClass(String className) {
			return new Locator(By.ByClassName.class, className);
		}

		public static Locator byCssSelector(String cssSelector) {
			return new Locator(By.ByCssSelector.class, cssSelector);
		}
	}

	protected static class PageElementHandler {

		private static final Log staticLogger = LogFactory.getLog(AbstractPage.class);

		private final WebDriver driver;
		private final WebDriverWait driverWait;
		private final long afterActionTime;

		private ApplicationContext applicationContext;
		private final PageElement rootElement;

		public PageElementHandler(WebDriver driver, WebDriverWait driverWait, long afterActionTime) {

			if (driver == null || driverWait == null) {
				throw new IllegalArgumentException("Driver and driverWait cannot be null");
			}

			this.driver = driver;
			this.driverWait = driverWait;
			this.afterActionTime = afterActionTime;

			this.rootElement = null;
		}

		public PageElementHandler(PageElementHandler elementHandler, PageElement rootElement) {

			this.driver = elementHandler.driver;
			this.driverWait = elementHandler.driverWait;
			this.afterActionTime = elementHandler.afterActionTime;
			this.applicationContext = elementHandler.applicationContext;

			this.rootElement = rootElement;
		}

		void setApplicationContext(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		long getAfterActionTime() {
			return afterActionTime;
		}

		public boolean existsElement(Locator... locators) {

			boolean exists = true;

			try {
				PageElement parentElement = getParentElement(rootElement, locators);
				exists = existsElement(parentElement, locators[locators.length - 1]);

			} catch (TimeoutException e) {
				exists = false;
			}

			return exists;
		}

		public int getElementCount(Locator... locators) {

			int count = 0;

			try {
				PageElement parentElement = getParentElement(rootElement, locators);
				count = getElementCount(parentElement, locators[locators.length - 1]);

			} catch (TimeoutException e) {
				count = 0;
			}

			return count;
		}

		public PageElement getElement(Locator... locators) throws TimeoutException {

			PageElement parentElement = getParentElement(rootElement, locators);
			PageElement element = getElement(parentElement, locators[locators.length - 1]);

			return element;
		}

		public List<PageElement> getElements(Locator... locators) throws TimeoutException {

			PageElement parentElement = getParentElement(rootElement, locators);
			List<PageElement> elements = getElements(parentElement, locators[locators.length - 1]);

			return elements;
		}

		private boolean existsElement(PageElement parentElement, Locator elementLocator) {

			boolean exists = true;
			By by = getLocatorByPlaceholder(elementLocator);

			try {
				if (parentElement != null) {

					WebElement element = parentElement.getWebElement();
					waitForSubElement(element, by);
				} else {
					waitForElement(by);
				}
			} catch (TimeoutException e) {
				exists = false;
			}

			return exists;
		}

		private int getElementCount(PageElement parentElement, Locator elementLocator) {

			int count = 0;

			By by = getLocatorByPlaceholder(elementLocator);

			if (existsElement(elementLocator)) {

				List<WebElement> elements = null;

				if (parentElement != null) {
					WebElement element = parentElement.getWebElement();
					elements = element.findElements(by);
				} else {
					elements = driver.findElements(by);
				}
				count = elements.size();
			}

			return count;
		}

		private PageElement getElement(PageElement parentElement, Locator elementLocator) {

			By by = getLocatorByPlaceholder(elementLocator);
			WebElement element = null;

			if (parentElement != null) {
				WebElement parent = parentElement.getWebElement();
				waitForSubElement(parent, by);
				element = parent.findElement(by);
			} else {
				waitForElement(by);
				element = driver.findElement(by);
			}
			PageElement pageElement = new PageElement(element, afterActionTime);

			return pageElement;
		}

		private List<PageElement> getElements(PageElement parentElement, Locator elementLocator) {

			By by = getLocatorByPlaceholder(elementLocator);

			List<WebElement> elements = null;

			if (parentElement != null) {
				WebElement parent = parentElement.getWebElement();
				waitForSubElement(parent, by);
				elements = parent.findElements(by);

			} else {
				waitForElement(by);
				elements = driver.findElements(by);
			}

			List<PageElement> pageElements = new ArrayList<>();

			for (WebElement element : elements) {
				PageElement pageElement = new PageElement(element, afterActionTime);

				pageElements.add(pageElement);
			}

			return Collections.unmodifiableList(pageElements);
		}

		private PageElement getParentElement(PageElement parentElement, Locator[] locators) throws TimeoutException {

			if (locators == null || locators.length == 0) {
				throw new IllegalArgumentException("Locator... cannot be null or empty");
			}

			int i = 0;
			while (i < locators.length - 1) {
				Locator locator = locators[i];
				parentElement = getElement(parentElement, locator);
				i++;
			}

			return parentElement;
		}

		private void waitForElement(By by) throws TimeoutException {

			staticLogger.info(String.format("Waiting for page element '%s'", by));

			long start = new Date().getTime();

			driverWait.until(ExpectedConditions.presenceOfElementLocated(by));

			long end = new Date().getTime();
			long waitTime = end - start;

			staticLogger.info(String.format("Found page element %s - Waited %s milliseconds", by, waitTime));
		}

		private void waitForSubElement(final WebElement element, final By by) throws TimeoutException {

			staticLogger.info(String.format("Waiting for nested page element '%s' under '%s'", by, element));

			long start = new Date().getTime();

			ExpectedCondition<WebElement> presenceOfSubElement = new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return element.findElement(by);
				}
			};

			driverWait.until(presenceOfSubElement);

			long end = new Date().getTime();
			long waitTime = end - start;

			staticLogger.info(String.format("Found nested page element '%s' - Waited %s milliseconds", by, waitTime));
		}

		private By getLocatorByPlaceholder(Locator elementLocator) {

			String value = elementLocator.getValue();
			if (applicationContext != null) {
				value = applicationContext.getEnvironment().resolvePlaceholders(value);
			}

			Class<? extends By> byClass = elementLocator.getByClass();
			try {
				Constructor<? extends By> constructor = byClass.getConstructor(String.class);
				By byLocator = constructor.newInstance(value);

				return byLocator;

			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
