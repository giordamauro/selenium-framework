package com.mgiorda.page.element;

import com.mgiorda.page.AbstractElement;

public class TextField extends AbstractElement {

	public void sendKeys(CharSequence... keysToSend) {
		pageElement.sendKeys(keysToSend);
	}

	public void submit() {
		pageElement.submit();
	}
}
