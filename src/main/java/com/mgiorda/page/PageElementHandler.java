package com.mgiorda.page;

public interface PageElementHandler extends AbstractElementHandler {

	<T extends AbstractPage> T getPageAs(Class<T> pageClass, Locator... locators) throws ElementTimeoutException;

}
