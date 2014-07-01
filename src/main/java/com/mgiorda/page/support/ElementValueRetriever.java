package com.mgiorda.page.support;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.AbstractElementHandler;
import com.mgiorda.page.Locator;
import com.mgiorda.page.element.Label;

public class ElementValueRetriever implements ValueRetriever {

	private final AbstractElementHandler elementHandler;

	public ElementValueRetriever(AbstractElementHandler elementHandler) {
		this.elementHandler = elementHandler;
	}

	@Override
	public Object getValueForLocators(Field field, Locator[] locators) {

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

}
