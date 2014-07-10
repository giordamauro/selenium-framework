package com.mgiorda.page;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageElement {

	private static final Log logger = LogFactory.getLog(PageElement.class);
	private static final long DEFAULT_AFTER_ACTION_MILLIS = 500;

	private final WebElement element;

	public PageElement(WebElement element) {
		this.element = element;
	}

	public WebElement getWebElement() {
		return element;
	}

	public void click(long afterActionMillis) {

		logger.debug(String.format("Clicking element '%s'", element));

		element.click();
		waitForActionTime(afterActionMillis);
	}

	public void click() {
		click(DEFAULT_AFTER_ACTION_MILLIS);
	}

	public void submit(long afterActionMillis) {

		logger.debug(String.format("Submitting element '%s'", element));

		element.submit();
		waitForActionTime(afterActionMillis);

	}

	public void submit() {
		submit(DEFAULT_AFTER_ACTION_MILLIS);
	}

	public void sendKeys(CharSequence... keysToSend) {

		if (logger.isDebugEnabled()) {
			String keys = "";
			for (CharSequence seq : keysToSend) {
				keys += seq.toString();
			}
			logger.debug(String.format("Sending keys '%s' to element '%s'", keys, element));
		}

		element.sendKeys(keysToSend);
	}

	public void hover(long afterActionMillis) {

		logger.debug(String.format("Hovering over element '%s'", element));
		try {

			InputStream scriptStream = this.getClass().getResourceAsStream("hover-script.js");
			String code = IOUtils.toString(scriptStream, "UTF-8");

			JavascriptExecutor js = ((JavascriptExecutor) getDriver());
			js.executeScript(code, element);

			waitForActionTime(afterActionMillis);

		} catch (IOException e) {
			throw new IllegalStateException("Couldn't hover on pageElement", e);
		}
	}

	public void hover() {
		hover(0L);
	}

	public void moveToElement(long afterActionMillis) {

		logger.debug(String.format("Moving to element '%s'", element));

		Actions action = new Actions(getDriver());
		action.moveToElement(element).build().perform();

		waitForActionTime(afterActionMillis);
	}

	public void moveToElement() {
		moveToElement(0L);
	}

	public void clear() {
		logger.debug(String.format("Clearing element '%s'", element));

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

	public void waitUntilVisible() {

		WebDriverWait wait = new WebDriverWait(getDriver(), 30);
		wait.until(ExpectedConditions.visibilityOf(element));
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