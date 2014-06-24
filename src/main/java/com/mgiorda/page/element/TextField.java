package com.mgiorda.page.element;

import com.mgiorda.test.AbstractElement;

public class TextField extends AbstractElement {

	public TextField(PageElement pageElement) {
		super(pageElement);
	}

	public void sendKeys(CharSequence... keysToSend) {
		pageElement.sendKeys(keysToSend);
	}

	public void submit() {
		pageElement.submit();
	}
}
