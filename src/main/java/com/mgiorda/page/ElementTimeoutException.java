package com.mgiorda.page;

import java.util.List;

public class ElementTimeoutException extends RuntimeException {

	private static final long serialVersionUID = 4703626638337761022L;

	private final int waitTimeOutSeconds;

	private final List<Locator> locators;

	public ElementTimeoutException(List<Locator> locators, int waitTimeOutSeconds) {

		super(String.format("Timed out after %s seconds expecting for %s", waitTimeOutSeconds, locators));

		this.waitTimeOutSeconds = waitTimeOutSeconds;
		this.locators = locators;
	}

	public int getWaitTimeOutSeconds() {
		return waitTimeOutSeconds;
	}

	public List<Locator> getLocators() {
		return locators;
	}
}
