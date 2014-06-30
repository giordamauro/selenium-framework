package com.mgiorda.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;

public class PageElement {

	private static final Log logger = LogFactory.getLog(PageElement.class);
	private static final long DEFAULT_AFTER_ACTION_MILLIS = 500;

	private final WebElement element;

	PageElement(WebElement element) {
		this.element = element;
	}

	public WebElement getWebElement() {
		return element;
	}

	public void click(long afterActionMillis) {

		logger.trace(String.format("Clicking element '%s'", element));

		element.click();
		waitForActionTime(afterActionMillis);
	}

	public void click() {
		click(DEFAULT_AFTER_ACTION_MILLIS);
	}

	public void submit(long afterActionMillis) {

		logger.trace(String.format("Submitting element '%s'", element));

		element.submit();
		waitForActionTime(afterActionMillis);

	}

	public void submit() {
		submit(DEFAULT_AFTER_ACTION_MILLIS);
	}

	public void sendKeys(CharSequence... keysToSend) {

		String keys = "";
		for (CharSequence seq : keysToSend) {
			keys += seq.toString();
		}
		logger.trace(String.format("Sending keys '%s' to element '%s'", keys, element));

		element.sendKeys(keysToSend);
	}

	public void clear() {
		logger.trace(String.format("Clearing element '%s'", element));

		element.clear();
	}

	public void moveOver() {
		logger.trace(String.format("Moving mouse over element '%s'", element));

		Actions action = new Actions(getDriver());
		action.moveToElement(element).build().perform();
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

	private void waitForActionTime(long afterActionTime) {
		try {
			Thread.sleep(afterActionTime);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private WebDriver getDriver() {
		WrapsDriver driverWrapper = (WrapsDriver) element;
		WebDriver driver = driverWrapper.getWrappedDriver();

		return driver;
	}
}