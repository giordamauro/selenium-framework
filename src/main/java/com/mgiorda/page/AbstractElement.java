package com.mgiorda.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.page.injector.PageElementValueRetriever;
import com.mgiorda.page.injector.ValueRetriever;

public abstract class AbstractElement {

	protected final Log logger = LogFactory.getLog(this.getClass());

	protected PageElement pageElement;
	protected AbstractElementHandler elementHandler;
	private ElementInjector elementInjector;

	private Class<?> containerClass;
	private String elementName;

	public AbstractElement() {

	}

	public void moveToElement() {

		logger.info("Moving to element" + getNameInfo());

		pageElement.moveToElement();
	}

	public void hover(long afterTimeMillis) {

		logger.info("Hovering over element" + getNameInfo());

		pageElement.hover(afterTimeMillis);
	}

	void setAbstractElement(AbstractElementHandler elementHandler, ElementInjector elementInjector, PageElement pageElement) {

		this.elementHandler = elementHandler;
		this.elementInjector = elementInjector;
		this.pageElement = pageElement;
	}

	void setFieldName(Class<?> containerClass, String elementName) {

		this.containerClass = containerClass;
		this.elementName = elementName;
	}

	protected void fetchElement(String fieldName) {

		ValueRetriever elementValueRetriever = new PageElementValueRetriever(elementHandler);
		elementInjector.autowireField(elementValueRetriever, this, fieldName);
	}

	protected String getNameInfo() {

		String message = "";

		if (containerClass != null && elementName != null) {

			message = String.format(" '%s' in %s", elementName, containerClass.getName());
		}

		return message;
	}

}