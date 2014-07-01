package com.mgiorda.page;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;
import com.mgiorda.page.element.Label;
import com.mgiorda.page.support.ValueRetriever;

class ElementInjector {

	private static final Log logger = LogFactory.getLog(ElementInjector.class);

	private ElementInjector() {

	}

	public static void autowireLocators(ValueRetriever valueRetriever, Object target) {

		Class<?> objClass = target.getClass();

		Field[] declaredFields = objClass.getDeclaredFields();
		for (Field field : declaredFields) {

			Locate annotation = field.getAnnotation(Locate.class);
			if (annotation != null) {

				Locator[] locators = ElementInjector.getLocatorsFromAnnotation(annotation);
				if (locators.length == 0) {
					throw new IllegalStateException(String.format("Couldn't find an element locator for field '%s' in page class '%s'", field.getName(), objClass.getSimpleName()));
				}

				Object value = valueRetriever.getValueForLocators(field, locators);

				if (value == null) {
					throw new IllegalStateException(String.format("Cannot autowire field '%s' of type '%s' in '%s'", field.getName(), field.getType(), objClass));
				}

				ElementInjector.setField(target, field, value);
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

	private static void setField(Object target, Field field, Object value) {

		Class<?> fieldClass = field.getType();
		Class<?> valueClass = value.getClass();

		if (String.class.isAssignableFrom(fieldClass) && Label.class.isAssignableFrom(valueClass)) {
			Label labelValue = (Label) value;
			value = labelValue.getText();
		}

		boolean isFieldAccessible = field.isAccessible();

		field.setAccessible(true);
		try {

			logger.info(String.format("Setting '%s' field in '%s' class - Value: %s", field.getName(), target.getClass().getSimpleName(), value));
			field.set(target, value);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			throw new IllegalStateException(e);
		}
		field.setAccessible(isFieldAccessible);
	}

}
