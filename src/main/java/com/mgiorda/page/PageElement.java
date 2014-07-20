package com.mgiorda.page;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public interface PageElement {

	void click(long afterActionMillis);

	void click();

	void submit(long afterActionMillis);

	void submit();

	void sendKeys(CharSequence... keysToSend);

	void hover(long afterActionMillis);

	void hover();

	void moveToElement(long afterActionMillis);

	void moveToElement();

	void clear();

	String getTagName();

	String getAttribute(String name);

	boolean isSelected();

	boolean isEnabled();

	String getText();

	boolean isDisplayed();

	Point getLocation();

	Dimension getSize();

	String getCssValue(String propertyName);
}