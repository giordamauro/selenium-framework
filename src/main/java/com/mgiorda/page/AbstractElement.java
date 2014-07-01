package com.mgiorda.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractElement {

	protected final Log logger = LogFactory.getLog(this.getClass());

	protected PageElement pageElement;
	protected AbstractElementHandler elementHandler;

	public AbstractElement() {

	}

	void setAbstractElement(AbstractElementHandler elementHandler, PageElement pageElement) {

		this.elementHandler = elementHandler;
		this.pageElement = pageElement;
	}

}