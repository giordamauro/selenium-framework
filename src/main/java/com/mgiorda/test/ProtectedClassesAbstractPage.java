package com.mgiorda.test;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class ProtectedClassesAbstractPage {

	protected static final class PageElement {

		private static final Log logger = LogFactory.getLog(PageElement.class);

		private final AbstractPage page;
		private final WebElement element;

		PageElement(AbstractPage page, WebElement element) {
			this.page = page;
			this.element = element;
		}

		AbstractPage getPage() {
			return page;
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

		public AbstractElement(PageElement pageElement) {
			this.pageElement = pageElement;
		}

		protected boolean existsElement(Locator elementLocator) {

			AbstractPage page = pageElement.getPage();
			boolean exists = page.existsSubElement(pageElement, elementLocator);

			return exists;
		}

		protected int countElements(Locator elementLocator) {

			AbstractPage page = pageElement.getPage();
			int count = page.getSubElementCount(pageElement, elementLocator);

			return count;
		}

		protected PageElement getElement(Locator elementLocator) {

			AbstractPage page = pageElement.getPage();
			PageElement element = page.getSubElement(pageElement, elementLocator);

			return element;
		}

		protected List<PageElement> getElements(Locator elementLocator) {

			AbstractPage page = pageElement.getPage();
			List<PageElement> elements = page.getSubElements(pageElement, elementLocator);

			return elements;
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
