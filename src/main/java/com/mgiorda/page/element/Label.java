package com.mgiorda.page.element;

import com.mgiorda.test.AbstractElement;

public class Label extends AbstractElement {

	public Label(PageElement pageElement) {
		super(pageElement);
	}

	public String getText() {
		return pageElement.getText();
	}

}
