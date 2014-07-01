package com.mgiorda.page;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.page.support.ElementValueRetriever;
import com.mgiorda.page.support.ValueRetriever;

public abstract class AbstractElement {

	protected final Log logger = LogFactory.getLog(this.getClass());

	protected PageElement pageElement;
	protected AbstractElementHandler elementHandler;

	public AbstractElement() {

	}

	void setAbstractElement(AbstractElementHandler elementHandler, PageElement pageElement) {

		this.elementHandler = elementHandler;
		this.pageElement = pageElement;

		ValueRetriever elementValueRetriever = new ElementValueRetriever(elementHandler);
		ElementInjector.autowireLocators(elementValueRetriever, this);

		callAfterPropertiesSet();
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