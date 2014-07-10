package com.mgiorda.page.elements;

import com.mgiorda.page.AbstractElement;

public class TextField extends AbstractElement {

	protected void afterPropertiesSet() {
		verifyTagName("input");
	}

	public void sendKeys(CharSequence... keysToSend) {

		String keys = "";
		for (CharSequence seq : keysToSend) {
			keys += seq.toString();
		}

		logger.info(String.format("Sending keys: '%s' to element", keys) + getNameInfo());

		pageElement.sendKeys(keysToSend);
	}

	public void submit() {

		logger.info("Submitting on element" + getNameInfo());

		pageElement.submit();
	}
}
