package com.mgiorda.page;

public class Locator {

	private final String value;
	private final ByType byType;

	private Locator(ByType byType, String value) {

		if (byType == null || value == null) {
			throw new IllegalArgumentException("Locator by and value cannot be null");
		}

		this.byType = byType;
		this.value = value;
	}

	public ByType getBy() {
		return byType;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		String toString = String.format("Locator by %s: '%s'", byType, value);

		return toString;
	}

	public static Locator byId(String id) {
		return new Locator(ByType.ID, id);
	}

	public static Locator byLinkText(String linkText) {
		return new Locator(ByType.LINK_TEXT, linkText);
	}

	public static Locator byPartialLinkText(String partialLinkText) {
		return new Locator(ByType.PARTIAL_LINK_TEXT, partialLinkText);
	}

	public static Locator byName(String name) {
		return new Locator(ByType.NAME, name);
	}

	public static Locator byTagName(String tagName) {
		return new Locator(ByType.TAG_NAME, tagName);
	}

	public static Locator byXpath(String xpath) {
		return new Locator(ByType.XPATH, xpath);
	}

	public static Locator byClass(String className) {
		return new Locator(ByType.CLASS_NAME, className);
	}

	public static Locator byCssSelector(String cssSelector) {
		return new Locator(ByType.CSS_SELECTOR, cssSelector);
	}
}