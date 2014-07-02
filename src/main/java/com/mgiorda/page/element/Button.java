package com.mgiorda.page.element;

import com.mgiorda.page.AbstractElement;

public class Button extends AbstractElement {

	public void click() {

		logger.info(String.format("Clicking button - %s", this));

		pageElement.click();
	}

	public void submit() {

		logger.info("Submitting button");

		pageElement.submit();
	}
}
