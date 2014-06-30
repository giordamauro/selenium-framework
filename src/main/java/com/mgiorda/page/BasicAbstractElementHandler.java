package com.mgiorda.page;

import java.util.List;

public interface BasicAbstractElementHandler {

	boolean existsElement(Locator... locators);

	int getElementCount(Locator... locators);

	PageElement getElement(Locator... locators) throws ElementTimeoutException;

	List<PageElement> getElements(Locator... locators) throws ElementTimeoutException;

}