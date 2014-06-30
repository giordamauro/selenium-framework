package com.mgiorda.page;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.annotation.By;
import com.mgiorda.annotation.Locate;
import com.mgiorda.page.element.Label;
import com.mgiorda.test.AbstractElement;

class AnnotationsUtil {

	private static final Log logger = LogFactory.getLog(AnnotationsUtil.class);

	private AnnotationsUtil() {

	}

	public static Locator[] getLocatorsFromAnnotation(Locate annotation) {

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

	public static Object getLocatorElementForHandler(Field field, AbstractElementHandler elementHandler, Locator[] locators) {

		Object value = null;

		Class<?> fieldClass = field.getType();

		if (Boolean.class.isAssignableFrom(fieldClass) || fieldClass.equals(boolean.class)) {
			value = elementHandler.existsElement(locators);

		} else if (Integer.class.isAssignableFrom(fieldClass) || fieldClass.equals(int.class)) {
			value = elementHandler.getElementCount(locators);

		} else if (String.class.isAssignableFrom(fieldClass)) {
			value = elementHandler.getElementAs(Label.class, locators);

		} else if (List.class.isAssignableFrom(fieldClass)) {

			ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
			Class<?> listClass = (Class<?>) fieldType.getActualTypeArguments()[0];

			if (String.class.isAssignableFrom(listClass)) {
				value = elementHandler.getElementsAs(Label.class, locators);

			} else if (AbstractElement.class.isAssignableFrom(listClass)) {

				@SuppressWarnings("unchecked")
				Class<? extends AbstractElement> elementClass = (Class<? extends AbstractElement>) listClass;
				value = elementHandler.getElementsAs(elementClass, locators);
			}

		} else if (AbstractElement.class.isAssignableFrom(fieldClass)) {

			@SuppressWarnings("unchecked")
			Class<? extends AbstractElement> elementClass = (Class<? extends AbstractElement>) fieldClass;
			value = elementHandler.getElementAs(elementClass, locators);
		}

		return value;
	}

	public static void setField(Object target, Field field, Object value) {

		Class<?> fieldClass = field.getType();
		Class<?> valueClass = value.getClass();

		if (String.class.isAssignableFrom(fieldClass) && Label.class.isAssignableFrom(valueClass)) {
			Label labelValue = (Label) value;
			value = labelValue.getText();
		}

		boolean isFieldAccessible = field.isAccessible();

		field.setAccessible(true);
		try {

			logger.info(String.format("Setting annotated field '%s' in class '%s' with value '%s'", field.getName(), target.getClass().getSimpleName(), value));
			field.set(target, value);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			throw new IllegalStateException(e);
		}
		field.setAccessible(isFieldAccessible);
	}

}
