package com.mgiorda.page.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;

import com.mgiorda.context.SpringUtil;
import com.mgiorda.page.ElementTimeoutException;
import com.mgiorda.page.Locator;
import com.mgiorda.page.PageElement;

public class DriverElementHandler {

	private static final Log staticLogger = LogFactory.getLog(DriverElementHandler.class);

	private final WebDriver driver;
	private final WebDriverWait driverWait;

	private final PageElement rootElement;

	private ApplicationContext applicationContext;

	public DriverElementHandler(WebDriver driver, WebDriverWait driverWait, ApplicationContext applicationContext) {

		if (driver == null) {
			throw new IllegalArgumentException("Driver cannot be null");
		}

		this.driver = driver;
		this.driverWait = driverWait;
		this.applicationContext = applicationContext;

		this.rootElement = null;
	}

	public DriverElementHandler(DriverElementHandler elementHandler, PageElement pageElement) {

		this.driver = elementHandler.driver;
		this.driverWait = elementHandler.driverWait;
		this.applicationContext = elementHandler.applicationContext;

		this.rootElement = pageElement;
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
			throw newElementTimeoutException(e, locators);
		}
	}

	public List<PageElement> getElements(Locator... locators) throws ElementTimeoutException {

		try {
			PageElement parentElement = getParentElement(rootElement, locators);
			List<PageElement> elements = getElements(parentElement, locators[locators.length - 1]);

			return elements;

		} catch (TimeoutException e) {
			throw newElementTimeoutException(e, locators);
		}
	}

	private boolean existsElement(PageElement parentElement, Locator elementLocator) {

		boolean exists = true;
		By by = getLocatorByPlaceholder(elementLocator);

		try {
			if (parentElement != null) {

				WebElement element = ((PageElementImpl) parentElement).getWebElement();
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
				WebElement element = ((PageElementImpl) parentElement).getWebElement();
				elements = element.findElements(by);
			} else {
				elements = driver.findElements(by);
			}
			count = elements.size();
		}

		return count;
	}

	private PageElementImpl getElement(PageElement parentElement, Locator elementLocator) {

		By by = getLocatorByPlaceholder(elementLocator);
		WebElement element = null;

		if (parentElement != null) {
			WebElement parent = ((PageElementImpl) parentElement).getWebElement();
			waitForSubElement(parent, by);
			element = parent.findElement(by);
		} else {
			waitForElement(by);
			element = driver.findElement(by);
		}
		PageElementImpl PageElementImpl = new PageElementImpl(element);

		return PageElementImpl;
	}

	private List<PageElement> getElements(PageElement parentElement, Locator elementLocator) {

		By by = getLocatorByPlaceholder(elementLocator);

		List<WebElement> elements = null;

		if (parentElement != null) {
			WebElement parent = ((PageElementImpl) parentElement).getWebElement();
			waitForSubElement(parent, by);
			elements = parent.findElements(by);

		} else {
			waitForElement(by);
			elements = driver.findElements(by);
		}

		List<PageElement> pageElements = new ArrayList<>();

		for (WebElement element : elements) {
			PageElementImpl PageElementImpl = new PageElementImpl(element);

			pageElements.add(PageElementImpl);
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

		staticLogger.debug(String.format("Waiting for page element '%s'", by));

		long start = new Date().getTime();

		if (driverWait != null) {
			try {
				driverWait.until(ExpectedConditions.presenceOfElementLocated(by));
			} catch (UnhandledAlertException e) {

				staticLogger.warn(String.format("Unexpected alert open: '%s'", e.getAlertText()));
				throw e;
			}
		}

		long end = new Date().getTime();
		long waitTime = end - start;

		staticLogger.debug(String.format("Found page element %s - Waited %s milliseconds", by, waitTime));
	}

	private void waitForSubElement(final WebElement element, final By by) throws TimeoutException {

		staticLogger.debug(String.format("Waiting for nested page element '%s' under '%s'", by, element));

		long start = new Date().getTime();

		if (driverWait != null) {
			ExpectedCondition<WebElement> presenceOfSubElement = new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return element.findElement(by);
				}
			};

			try {
				driverWait.until(presenceOfSubElement);
			} catch (UnhandledAlertException e) {

				staticLogger.warn(String.format("Unexpected alert open: '%s'", e.getAlertText()));
				throw e;
			}
		}

		long end = new Date().getTime();
		long waitTime = end - start;

		staticLogger.debug(String.format("Found nested page element '%s' - Waited %s milliseconds", by, waitTime));
	}

	private By getLocatorByPlaceholder(Locator elementLocator) {

		String value = elementLocator.getValue();
		if (applicationContext != null) {
			value = SpringUtil.getPropertyPlaceholder(applicationContext, value);
		}

		By by = ByLocatorAdapter.newBy(elementLocator.getBy(), value);

		return by;
	}

	private static ElementTimeoutException newElementTimeoutException(TimeoutException e, Locator[] locators) {

		if (staticLogger.isDebugEnabled()) {
			staticLogger.warn(e);
		}

		List<Locator> locatorsList = getAsLocatableList(locators);
		int waitTimeOut = getTimeoutSeconds(e);

		ElementTimeoutException exception = new ElementTimeoutException(locatorsList, waitTimeOut);

		return exception;
	}

	private static List<Locator> getAsLocatableList(Locator[] locators) {

		List<Locator> list = new ArrayList<>();

		for (Locator locator : locators) {
			list.add(locator);
		}

		return list;
	}

	private static int getTimeoutSeconds(TimeoutException e) {

		String message = e.getMessage();
		message = message.split("Timed out after")[1];
		message = message.split("seconds waiting")[0];

		int seconds = Integer.valueOf(message.trim());

		return seconds;
	}
}