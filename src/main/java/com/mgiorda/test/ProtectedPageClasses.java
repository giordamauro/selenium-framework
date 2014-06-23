package com.mgiorda.test;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

abstract class ProtectedPageClasses {

	protected static final class PageElement {

		private static final Log logger = LogFactory.getLog(PageElement.class);

		private final WebElement element;

		PageElement(WebElement element) {
			this.element = element;
		}

		WebElement getWebElement() {
			return element;
		}

		public void click() {
			logger.info(String.format("PageElement(%s) - Clicking", this.hashCode()));

			element.click();
		}

		public void submit() {
			logger.info(String.format("PageElement(%s) - Submitting", this.hashCode()));

			element.submit();
		}

		public void sendKeys(CharSequence... keysToSend) {

			String keys = "";
			for (CharSequence seq : keysToSend) {
				keys += seq.toString();
			}
			logger.info(String.format("PageElement(%s) - Sending keys '%s'", this.hashCode(), keys));

			element.sendKeys(keysToSend);
		}

		public void clear() {
			logger.info(String.format("PageElement(%s) - Clearing", this.hashCode()));

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
	}

	protected static abstract class AbstractElement {

		protected final Log logger = LogFactory.getLog(this.getClass());

		protected final PageElement pageElement;
		protected PageElementHandler elementHandler;

		public AbstractElement(PageElement pageElement) {
			this.pageElement = pageElement;
		}

		void setElementHandler(PageElementHandler elementHandler) {
			this.elementHandler = new PageElementHandler(elementHandler, pageElement);
		}

		public static <T extends AbstractElement> T factory(Class<T> elementClass, PageElementHandler elementHandler, PageElement pageElement) {
			try {
				Constructor<T> constructor = elementClass.getConstructor(PageElement.class);
				boolean accessible = constructor.isAccessible();

				constructor.setAccessible(true);
				T newInstance = constructor.newInstance(pageElement);
				constructor.setAccessible(accessible);

				newInstance.setElementHandler(elementHandler);

				return newInstance;

			} catch (Exception e) {
				throw new IllegalStateException(e);
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
}
