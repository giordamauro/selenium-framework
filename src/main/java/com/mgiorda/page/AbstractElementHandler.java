package com.mgiorda.page;

import java.util.List;

import com.mgiorda.test.AbstractElement;

public interface AbstractElementHandler {

	boolean existsElement(Locator... locators);

	int getElementCount(Locator... locators);

	<T extends AbstractElement> T getElementAs(Class<T> elementClass, Locator... locators) throws ElementTimeoutException;

	<T extends AbstractElement> List<T> getElementsAs(Class<T> elementClass, Locator... locators) throws ElementTimeoutException;
}
