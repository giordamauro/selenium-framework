package com.mgiorda.page;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;

public class BasicAbstractElementHandler {

	private static final Log staticLogger = LogFactory.getLog(BasicAbstractElementHandler.class);

	private final WebDriver driver;
	private final WebDriverWait driverWait;

	private final PageElement rootElement;

	private final ApplicationContext applicationContext;

	public BasicAbstractElementHandler(WebDriver driver, WebDriverWait driverWait, ApplicationContext applicationContext) {

		if (driver == null || driverWait == null) {
			throw new IllegalArgumentException("Driver and driverWait cannot be null");
		}

		this.driver = driver;
		this.driverWait = driverWait;

		this.applicationContext = applicationContext;
		this.rootElement = null;
	}

	public BasicAbstractElementHandler(BasicAbstractElementHandler elementHandler, PageElement rootElement) {

		this.driver = elementHandler.driver;
		this.driverWait = elementHandler.driverWait;
		this.applicationContext = elementHandler.applicationContext;

		this.rootElement = rootElement;
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

	public PageElement getElement(Locator... locators) throws ElementTimeoutException {

		try {
			PageElement parentElement = getParentElement(rootElement, locators);
			PageElement element = getElement(parentElement, locators[locators.length - 1]);

			return element;

		} catch (TimeoutException e) {
			throw new ElementTimeoutException(String.format("TimeoutException getting element for Locators '%s'", ArrayUtils.toString(locators)), e);
		}
	}

	public List<PageElement> getElements(Locator... locators) throws TimeoutException {

		try {
			PageElement parentElement = getParentElement(rootElement, locators);
			List<PageElement> elements = getElements(parentElement, locators[locators.length - 1]);

			return elements;

		} catch (TimeoutException e) {
			throw new ElementTimeoutException(String.format("TimeoutException getting elements for Locators '%s'", ArrayUtils.toString(locators)), e);
		}
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
		PageElement pageElement = new PageElement(element);

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
			PageElement pageElement = new PageElement(element);

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

		staticLogger.trace(String.format("Waiting for page element '%s'", by));

		long start = new Date().getTime();

		if (driverWait != null) {
			driverWait.until(ExpectedConditions.presenceOfElementLocated(by));
		}

		long end = new Date().getTime();
		long waitTime = end - start;

		staticLogger.trace(String.format("Found page element %s - Waited %s milliseconds", by, waitTime));
	}

	private void waitForSubElement(final WebElement element, final By by) throws TimeoutException {

		staticLogger.trace(String.format("Waiting for nested page element '%s' under '%s'", by, element));

		long start = new Date().getTime();

		if (driverWait != null) {
			ExpectedCondition<WebElement> presenceOfSubElement = new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return element.findElement(by);
				}
			};

			driverWait.until(presenceOfSubElement);
		}

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