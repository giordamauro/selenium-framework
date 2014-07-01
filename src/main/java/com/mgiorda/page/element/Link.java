package com.mgiorda.page.element;

import com.mgiorda.page.AbstractElement;

public class Link extends AbstractElement {

	public void click() {
		pageElement.click();
	}

	public String getLinkText() {
		String text = pageElement.getText();

		return text;
	}
}
