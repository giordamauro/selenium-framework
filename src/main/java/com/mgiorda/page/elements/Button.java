package com.mgiorda.page.elements;

import com.mgiorda.page.AbstractElement;

public class Button extends AbstractElement {

	protected void afterPropertiesSet() {
		verifyTagName("input");
	}

	public void click() {

		logger.info("Clicking on element" + getNameInfo());

		pageElement.click();
	}

	public void submit() {

		logger.info("Submitting button element" + getNameInfo());

		pageElement.submit();
	}
}
