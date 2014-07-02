package com.mgiorda.page.element;

import com.mgiorda.page.AbstractElement;

public class TextField extends AbstractElement {

	public void sendKeys(CharSequence... keysToSend) {

		String keys = "";
		for (CharSequence seq : keysToSend) {
			keys += seq.toString();
		}

		logger.info(String.format("Sending textField keys: '%s' - %s", keys, this));

		pageElement.sendKeys(keysToSend);
	}

	public void submit() {
		pageElement.submit();
	}
}
