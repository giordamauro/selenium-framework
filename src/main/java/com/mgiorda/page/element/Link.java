package com.mgiorda.page.element;

import com.mgiorda.page.AbstractElement;

public class Link extends AbstractElement {

	public void click() {

		logger.info(String.format("Clicking on link - %s", this));

		pageElement.click();
	}

	public String getLinkText() {
		String text = pageElement.getText();

		return text;
	}
}
