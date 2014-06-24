package com.mgiorda.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.annotation.By;
import com.mgiorda.annotation.Locate;
import com.mgiorda.test.ProtectedPageClasses.Locator;
import com.mgiorda.test.ProtectedPageClasses.PageElement;
import com.mgiorda.test.ProtectedPageClasses.PageElementHandler;

class AnnotationsSupport {

	private static final Log logger = LogFactory.getLog(AnnotationsSupport.class);

	private AnnotationsSupport() {

	}

	public static <T extends AbstractPage> void initLocators(T page) {

		Class<?> pageClass = page.getClass();

		Field[] declaredFields = pageClass.getDeclaredFields();
		for (Field field : declaredFields) {

			Locate annotation = field.getAnnotation(Locate.class);
			if (annotation != null) {

				Locator[] locators = getLocatorsFromAnnotation(annotation);
				if (locators.length == 0) {
					throw new IllegalStateException(String.format("Couldn't find an element locator for field '%s' in page class '%s'", field.getName(), pageClass.getSimpleName()));
				}

				Object value = getLocatorElement(field.getType(), page, locators);
				if (value == null) {
					throw new IllegalStateException(String.format("Cannot autowire field '%s' of type '%s' in page '%s'", field.getName(), field.getType(), pageClass));
				}

				setField(page, field, value);
			}
		}
	}

	public static <T extends AbstractElement> void initElementLocators(T element) {

		Class<?> pageClass = element.getClass();

		Field[] declaredFields = pageClass.getDeclaredFields();
		for (Field field : declaredFields) {

			Locate annotation = field.getAnnotation(Locate.class);
			if (annotation != null) {

				Locator[] locators = getLocatorsFromAnnotation(annotation);
				if (locators.length == 0) {
					throw new IllegalStateException(String.format("Couldn't find an element locator for field '%s' in page class '%s'", field.getName(), pageClass.getSimpleName()));
				}

				Object value = getLocatorElementForHandler(field.getType(), element.getElementHandler(), locators);
				if (value == null) {
					throw new IllegalStateException(String.format("Cannot autowire field '%s' of type '%s' in page '%s'", field.getName(), field.getType(), pageClass));
				}

				setField(element, field, value);
			}
		}
	}

	private static Locator[] getLocatorsFromAnnotation(Locate annotation) {

		List<Locator> locators = new ArrayList<Locator>();
		By[] multipleLocators = annotation.value();

		for (By byAnnotation : multipleLocators) {
			Locator locator = getByAnnotation(byAnnotation);
			if (locator != null) {
				locators.add(locator);
			}
		}

		Locator[] locatorsArray = locators.toArray(new Locator[] {});

		return locatorsArray;
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

	private static Object getLocatorElement(Class<?> fieldType, AbstractPage page, Locator[] locators) {

		Object value = null;

		if (AbstractPage.class.isAssignableFrom(fieldType)) {

			@SuppressWarnings("unchecked")
			Class<? extends AbstractPage> pageClass = (Class<? extends AbstractPage>) fieldType;
			value = getValueForAbstractPage(pageClass, page, locators);

		} else {
			value = getLocatorElementForHandler(fieldType, page.getElementHandler(), locators);
		}

		return value;
	}

	private static Object getLocatorElementForHandler(Class<?> fieldType, PageElementHandler elementHandler, Locator[] locators) {

		Object value = null;

		if (fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)) {
			value = elementHandler.existsElement(locators);

		} else if (fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)) {
			value = elementHandler.getElementCount(locators);

		} else if (fieldType.isAssignableFrom(PageElement.class)) {
			value = elementHandler.getElement(locators);

		} else if (fieldType.isAssignableFrom(List.class)) {
			value = elementHandler.getElements(locators);

		} else if (AbstractElement.class.isAssignableFrom(fieldType)) {

			@SuppressWarnings("unchecked")
			Class<? extends AbstractElement> elementClass = (Class<? extends AbstractElement>) fieldType;
			value = getValueForAbstractElement(elementClass, elementHandler, locators);
		}

		return value;
	}

	private static Object getValueForAbstractElement(Class<? extends AbstractElement> fieldType, PageElementHandler pageElementHandler, Locator[] locators) {

		PageElement pageElement = pageElementHandler.getElement(locators);
		Object element = AbstractElement.factory(fieldType, pageElementHandler, pageElement);

		return element;
	}

	private static Object getValueForAbstractPage(Class<? extends AbstractPage> fieldType, AbstractPage page, Locator[] locators) {

		PageElement pageElement = page.getElementHandler().getElement(locators);
		Object element = AbstractPage.factory(fieldType, page, pageElement);

		return element;
	}

	private static void setField(Object target, Field field, Object value) {

		boolean isFieldAccessible = field.isAccessible();

		field.setAccessible(true);
		try {

			logger.info(String.format("Setting '%s' field in class '%s' with element '%s'", target.getClass().getSimpleName(), field.getName(), value));
			field.set(target, value);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			throw new IllegalStateException(e);
		}
		field.setAccessible(isFieldAccessible);
	}
}
