package com.mgiorda.page;

import java.util.List;

public interface AbstractElementFactory {

    <T extends AbstractElement> T adaptPageElementAs(Class<T> elementClass, PageElement pageElement);

    PageElement getPageElement(Locator... locators) throws ElementTimeoutException;

    List<PageElement> getPageElements(Locator... locators) throws ElementTimeoutException;

    AbstractElementFactory newElementFactory(PageElement pageElement);
}
