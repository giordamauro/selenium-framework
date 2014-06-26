package com.mgiorda.page.element;

import com.mgiorda.test.AbstractElement;

public class Link extends AbstractElement {

	public Link(PageElement pageElement) {
		super(pageElement);
	}

	public void click() {
		pageElement.click();
	}

	public String getLinkText() {
		String text = pageElement.getText();

		return text;
	}
}
