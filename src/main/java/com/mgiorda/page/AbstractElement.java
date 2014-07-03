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

	public AbstractElement() {

	}

	void setAbstractElement(AbstractElementHandler elementHandler, ElementInjector elementInjector, PageElement pageElement) {

		this.elementHandler = elementHandler;
		this.elementInjector = elementInjector;
		this.pageElement = pageElement;
	}

	protected void fetchElement(String fieldName) {

		ValueRetriever elementValueRetriever = new PageElementValueRetriever(elementHandler);
		elementInjector.autowireField(elementValueRetriever, this, fieldName);
	}

}