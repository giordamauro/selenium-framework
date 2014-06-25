package com.mgiorda.test;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	public static <T extends AbstractElement> T factoryGeneric(Class<T> elementClass, PageElementHandler elementHandler, T abstractElement, Class<?> constructorType) {
		try {
			Constructor<T> constructor = elementClass.getConstructor(constructorType);
			boolean accessible = constructor.isAccessible();

			constructor.setAccessible(true);
			T newInstance = constructor.newInstance(abstractElement);
			constructor.setAccessible(accessible);

			newInstance.setElementHandler(elementHandler);

			AnnotationsSupport.initElementLocators(newInstance);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}