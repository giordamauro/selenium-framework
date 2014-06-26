package com.mgiorda.test;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.page.element.Label;

public abstract class AbstractElement extends ProtectedPageClasses {

	protected final Log logger = LogFactory.getLog(this.getClass());

	protected final PageElement pageElement;
	protected PageElementHandler elementHandler;

	public AbstractElement(PageElement pageElement) {
		this.pageElement = pageElement;
		this.pageElement.setAbstractElement(this);
	}

	PageElementHandler getElementHandler() {
		return elementHandler;
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

			AnnotationsSupport.initElementLocators(newInstance);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> T factoryValue(Class<T> expectedClass, PageElementHandler elementHandler, PageElement pageElement) {

		if (AbstractElement.class.isAssignableFrom(expectedClass)) {

			@SuppressWarnings("unchecked")
			Class<? extends AbstractElement> absClass = (Class<? extends AbstractElement>) expectedClass;

			@SuppressWarnings("unchecked")
			T element = (T) AbstractElement.factory(absClass, elementHandler, pageElement);

			return element;

		} else if (String.class.isAssignableFrom(expectedClass)) {

			Label label = AbstractElement.factory(Label.class, elementHandler, pageElement);

			@SuppressWarnings("unchecked")
			T stringValue = (T) label.getText();

			return stringValue;
		} else {
			throw new IllegalStateException(String.format("Cannot factory value for class '%s': should be AbstractElement or String", expectedClass));
		}
	}
}