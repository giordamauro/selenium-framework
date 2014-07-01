package com.mgiorda.page;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.page.annotations.Locate;

public abstract class AbstractElement {

	protected final Log logger = LogFactory.getLog(this.getClass());

	protected PageElement pageElement;
	protected AbstractElementHandler elementHandler;

	void setAbstractElement(AbstractElementHandler elementHandler, PageElement pageElement) {

		this.elementHandler = elementHandler;
		this.pageElement = pageElement;

		autowireLocators();
		callAfterPropertiesSet();
	}

	private void autowireLocators() {

		Class<?> elementClass = this.getClass();

		Field[] declaredFields = elementClass.getDeclaredFields();
		for (Field field : declaredFields) {

			Locate annotation = field.getAnnotation(Locate.class);
			if (annotation != null) {

				Locator[] locators = AnnotationsUtil.getLocatorsFromAnnotation(annotation);
				if (locators.length == 0) {
					throw new IllegalStateException(String.format("Couldn't find an element locator for field '%s' in element class '%s'", field.getName(), elementClass.getSimpleName()));
				}

				Object value = AnnotationsUtil.getLocatorElementForHandler(field, elementHandler, locators);

				if (value == null) {
					throw new IllegalStateException(String.format("Cannot autowire field '%s' of type '%s' in element '%s'", field.getName(), field.getType(), elementClass));
				}

				AnnotationsUtil.setField(this, field, value);
			}
		}
	}

	private void callAfterPropertiesSet() {

		Class<? extends AbstractElement> elementClass = this.getClass();

		try {
			Method afterProperties = elementClass.getDeclaredMethod("afterPropertiesSet");
			if (afterProperties != null) {
				boolean methodAccessible = afterProperties.isAccessible();
				afterProperties.setAccessible(true);

				afterProperties.invoke(this);
				afterProperties.setAccessible(methodAccessible);
			}
		} catch (NoSuchMethodException e) {
			// method doesn't exist, nothing to do

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}