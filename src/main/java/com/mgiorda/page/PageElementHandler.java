package com.mgiorda.page;

public interface PageElementHandler {

	<T extends AbstractPage> T getPageAs(Class<T> pageClass, Locator... locators) throws ElementTimeoutException;
}
