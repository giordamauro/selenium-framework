package com.mgiorda.page.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;

import com.mgiorda.page.ByType;

class ByLocatorAdapter {

	private static final Map<ByType, Class<? extends By>> byClasses = new HashMap<>();

	static {

		byClasses.put(ByType.ID, By.ById.class);
		byClasses.put(ByType.CLASS_NAME, By.ByClassName.class);
		byClasses.put(ByType.CSS_SELECTOR, By.ByCssSelector.class);
		byClasses.put(ByType.LINK_TEXT, By.ByLinkText.class);
		byClasses.put(ByType.NAME, By.ByName.class);
		byClasses.put(ByType.PARTIAL_LINK_TEXT, By.ByPartialLinkText.class);
		byClasses.put(ByType.TAG_NAME, By.ByTagName.class);
		byClasses.put(ByType.XPATH, By.ByXPath.class);
	}

	private ByLocatorAdapter() {
	}

	public static By newBy(ByType byType, String value) {

		Class<? extends By> byClass = byClasses.get(byType);
		try {
			Constructor<? extends By> constructor = byClass.getConstructor(String.class);
			By byLocator = constructor.newInstance(value);

			return byLocator;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}