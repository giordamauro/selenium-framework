package com.mgiorda.page.element;

import com.mgiorda.test.AbstractElement;

public class Button extends AbstractElement {

	public Button(PageElement pageElement) {
		super(pageElement);
	}

	public void click() {
		pageElement.click();
	}

	public void submit() {
		pageElement.submit();
	}
}
