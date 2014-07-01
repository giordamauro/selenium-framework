package com.mgiorda.page;

import org.openqa.selenium.By;

public final class Locator {

	private final Class<? extends By> byClass;

	private final String value;

	private Locator(Class<? extends By> byClass, String value) {

		if (value == null) {
			throw new IllegalArgumentException("Locator value cannot be null");
		}
		this.byClass = byClass;
		this.value = value;
	}

	public Class<? extends By> getByClass() {
		return byClass;
	}

	public String getValue() {
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