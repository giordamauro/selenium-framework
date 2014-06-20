package com.mgiorda.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.TimeoutException;

import com.mgiorda.annotations.By;
import com.mgiorda.annotations.Locate;
import com.mgiorda.test.AbstractPage.AbstractElement;
import com.mgiorda.test.AbstractPage.Locator;
import com.mgiorda.test.AbstractPage.PageElement;

class AnnotationsSupport {

	private static final Log logger = LogFactory.getLog(AnnotationsSupport.class);

	private AnnotationsSupport() {

	}

	public static <T extends AbstractPage> void initLocateBy(T page) {

		Class<?> pageClass = page.getClass();

		Field[] declaredFields = pageClass.getDeclaredFields();
		for (Field field : declaredFields) {

			Locate annotation = field.getAnnotation(Locate.class);
			if (annotation != null) {

				List<Locator> elementLocators = getLocatorsFromAnnotation(annotation);
				if (elementLocators.isEmpty()) {
					throw new IllegalStateException(String.format("Couldn't find an element locator for field '%s' in page class '%s'", field.getName(), pageClass.getSimpleName()));
				}

				Object value = getLocatorElement(field.getType(), page, elementLocators);
				if (value == null) {
					throw new IllegalStateException(String.format("Cannot autowire field '%s' of type '%s' in page '%s'", field.getName(), field.getType(), pageClass));
				}

				setField(page, field, value);
			}
		}
	}

	private static List<Locator> getLocatorsFromAnnotation(Locate annotation) {

		List<Locator> locators = new ArrayList<Locator>();
		By[] multipleLocators = annotation.value();

		for (By byAnnotation : multipleLocators) {
			Locator locator = getByAnnotation(byAnnotation);
			if (locator != null) {
				locators.add(locator);
			}
		}

		return locators;
	}

	private static Locator getByAnnotation(By annotation) {

		Locator elementLocator = null;

		String id = annotation.id();
		String name = annotation.name();
		String css = annotation.css();
		String className = annotation.className();
		String tagName = annotation.tagName();
		String linkText = annotation.linkText();
		String partialLinkText = annotation.partialLinkText();
		String xpath = annotation.xpath();

		if (!id.equals("")) {
			elementLocator = Locator.byId(id);
		} else if (!name.equals("")) {
			elementLocator = Locator.byName(name);
		} else if (!css.equals("")) {
			elementLocator = Locator.byCssSelector(css);
		} else if (!className.equals("")) {
			elementLocator = Locator.byClass(className);
		} else if (!tagName.equals("")) {
			elementLocator = Locator.byTagName(tagName);
		} else if (!linkText.equals("")) {
			elementLocator = Locator.byLinkText(linkText);
		} else if (!partialLinkText.equals("")) {
			elementLocator = Locator.byPartialLinkText(partialLinkText);
		} else if (!xpath.equals("")) {
			elementLocator = Locator.byXpath(xpath);
		}

		return elementLocator;
	}

	private static <T extends AbstractPage> Object getLocatorElement(Class<?> fieldType, T page, List<Locator> elementLocators) {

		Object value = null;

		if (fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)) {
			value = getExistsElement(page, elementLocators);

		} else if (fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)) {
			value = getCountElement(page, elementLocators);

		} else if (fieldType.isAssignableFrom(PageElement.class)) {
			value = getPageElementValue(page, elementLocators);

		} else if (fieldType.isAssignableFrom(List.class)) {
			value = getPageElementsValue(page, elementLocators);

		} else if (fieldType.isAssignableFrom(AbstractElement.class)) {
			value = getValueForAbstractElement(fieldType, page, elementLocators);
		}

		return value;
	}

	private static <T extends AbstractPage> Object getValueForAbstractElement(Class<?> fieldType, T page, List<Locator> elementLocators) {

		PageElement pageElement = getPageElementValue(page, elementLocators);
		try {
			Constructor<?> constructor = fieldType.getConstructor(PageElement.class);
			Object newInstance = constructor.newInstance(pageElement);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static <T extends AbstractPage> int getCountElement(T page, List<Locator> elementLocators) {

		int count = 0;

		if (elementLocators.size() == 1) {
			count = page.getElementCount(elementLocators.get(0));

		} else {
			try {
				PageElement parentElement = getParentElement(page, elementLocators);
				count = page.getSubElementCount(parentElement, elementLocators.get(elementLocators.size() - 1));
			} catch (TimeoutException e) {
				count = 0;
			}
		}

		return count;
	}

	private static <T extends AbstractPage> boolean getExistsElement(T page, List<Locator> elementLocators) {

		boolean exists = true;

		if (elementLocators.size() == 1) {
			exists = page.existsElement(elementLocators.get(0));

		} else {
			try {
				PageElement parentElement = getParentElement(page, elementLocators);
				exists = page.existsSubElement(parentElement, elementLocators.get(elementLocators.size() - 1));
			} catch (TimeoutException e) {
				exists = false;
			}
		}

		return exists;
	}

	private static <T extends AbstractPage> PageElement getPageElementValue(T page, List<Locator> elementLocators) {

		PageElement pageElement = null;

		if (elementLocators.size() == 1) {
			pageElement = page.getElement(elementLocators.get(0));

		} else {
			try {
				PageElement parentElement = getParentElement(page, elementLocators);
				pageElement = page.getSubElement(parentElement, elementLocators.get(elementLocators.size() - 1));
			} catch (TimeoutException e) {
				pageElement = null;
			}
		}

		return pageElement;
	}

	private static <T extends AbstractPage> List<PageElement> getPageElementsValue(T page, List<Locator> elementLocators) {

		List<PageElement> pageElements = null;

		if (elementLocators.size() == 1) {

			pageElements = page.getElements(elementLocators.get(0));

		} else {
			try {
				PageElement parentElement = getParentElement(page, elementLocators);
				pageElements = page.getSubElements(parentElement, elementLocators.get(elementLocators.size() - 1));
			} catch (TimeoutException e) {
				pageElements = null;
			}
		}

		return pageElements;
	}

	private static <T extends AbstractPage> PageElement getParentElement(T page, List<Locator> elementLocators) throws TimeoutException {

		PageElement pageElement = page.getElement(elementLocators.get(0));

		int i = 1;
		while (i < elementLocators.size() - 1) {

			Locator locator = elementLocators.get(i);
			pageElement = page.getSubElement(pageElement, locator);

			i++;
		}

		return pageElement;
	}

	private static <T extends AbstractPage> void setField(T page, Field field, Object value) {

		boolean isFieldAccessible = field.isAccessible();

		field.setAccessible(true);
		try {

			logger.info(String.format("Setting '%s' page field '%s' with element '%s'", page.getClass().getSimpleName(), field.getName(), value));
			field.set(page, value);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			throw new IllegalStateException(e);
		}
		field.setAccessible(isFieldAccessible);
	}
}
