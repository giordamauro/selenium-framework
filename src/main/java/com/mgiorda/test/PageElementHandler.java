package com.mgiorda.test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import com.mgiorda.test.ProtectedPageClasses.AbstractElement;
import com.mgiorda.test.ProtectedPageClasses.Locator;
import com.mgiorda.test.ProtectedPageClasses.PageElement;

public class PageElementHandler {

	private static final Log staticLogger = LogFactory.getLog(AbstractPage.class);

	private final WebDriver driver;
	private final WebDriverWait driverWait;

	private final PageElement parentElement;
	private ApplicationContext applicationContext;

	public PageElementHandler(WebDriver driver, WebDriverWait driverWait) {

		if (driver == null || driverWait == null) {
			throw new IllegalArgumentException("Driver and driverWait cannot be null");
		}

		this.driver = driver;
		this.driverWait = driverWait;

		this.parentElement = null;
	}

	public PageElementHandler(PageElementHandler elementHandler, PageElement parentElement) {

		this.driver = elementHandler.driver;
		this.driverWait = elementHandler.driverWait;

		this.parentElement = parentElement;
		this.applicationContext = elementHandler.applicationContext;
	}

	void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean existsElement(Locator elementLocator) {

		boolean exists = true;
		By by = getLocatorByPlaceholder(elementLocator);

		try {
			waitForElement(by);

		} catch (TimeoutException e) {
			exists = false;
		}

		return exists;
	}

	public boolean existsSubElement(PageElement pageElement, Locator elementLocator) {

		boolean exists = true;

		WebElement element = pageElement.getWebElement();
		By by = getLocatorByPlaceholder(elementLocator);

		try {
			waitForSubElement(element, by);

		} catch (TimeoutException e) {
			exists = false;
		}

		return exists;
	}

	public int getElementCount(Locator elementLocator) {

		int count = 0;

		By by = getLocatorByPlaceholder(elementLocator);

		if (existsElement(elementLocator)) {

			List<WebElement> elements = driver.findElements(by);
			count = elements.size();
		}

		return count;
	}

	public PageElement getElement(Locator elementLocator) {

		By by = getLocatorByPlaceholder(elementLocator);
		waitForElement(by);

		WebElement element = driver.findElement(by);

		PageElement pageElement = new PageElement(element);

		return pageElement;
	}

	public List<PageElement> getElements(Locator elementLocator) {

		By by = getLocatorByPlaceholder(elementLocator);
		waitForElement(by);

		List<WebElement> elements = driver.findElements(by);

		List<PageElement> pageElements = new ArrayList<>();

		for (WebElement element : elements) {
			PageElement pageElement = new PageElement(element);

			pageElements.add(pageElement);
		}

		return Collections.unmodifiableList(pageElements);
	}

	public int getSubElementCount(PageElement pageElement, Locator elementLocator) {

		int count = 0;

		WebElement element = pageElement.getWebElement();
		By by = getLocatorByPlaceholder(elementLocator);

		if (existsElement(elementLocator)) {

			List<WebElement> elements = element.findElements(by);
			count = elements.size();
		}

		return count;
	}

	public PageElement getSubElement(PageElement pageElement, Locator elementLocator) {

		WebElement element = pageElement.getWebElement();
		By by = getLocatorByPlaceholder(elementLocator);

		waitForSubElement(element, by);

		WebElement subElement = element.findElement(by);
		PageElement pageSubElement = new PageElement(subElement);

		return pageSubElement;
	}

	public List<PageElement> getSubElements(PageElement pageElement, Locator elementLocator) {

		WebElement element = pageElement.getWebElement();
		By by = getLocatorByPlaceholder(elementLocator);

		waitForSubElement(element, by);

		List<WebElement> elements = element.findElements(by);

		List<PageElement> pageElements = new ArrayList<>();

		for (WebElement subElement : elements) {
			PageElement pageSubElement = new PageElement(subElement);

			pageElements.add(pageSubElement);
		}

		return Collections.unmodifiableList(pageElements);
	}

	public <T extends AbstractElement> T factoryAbstractElement(Class<T> elementClass, PageElement pageElement) {
		try {
			Constructor<T> constructor = elementClass.getConstructor(PageElement.class);
			T newInstance = constructor.newInstance(pageElement);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private void waitForElement(By by) throws TimeoutException {

		long start = new Date().getTime();

		driverWait.until(ExpectedConditions.presenceOfElementLocated(by));

		long end = new Date().getTime();
		long waitTime = end - start;

		staticLogger.info(String.format("Found '%s' page element %s - Waited %s milliseconds", this.getClass().getSimpleName(), by, waitTime));
	}

	private void waitForSubElement(final WebElement element, final By by) throws TimeoutException {

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

		staticLogger.info(String.format("Found '%s' nested page element '%s' - Waited %s milliseconds", this.getClass().getSimpleName(), by, waitTime));
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
