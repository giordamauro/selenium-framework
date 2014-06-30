package com.mgiorda.page;

import java.lang.reflect.Constructor;

import com.mgiorda.test.AbstractPage;

public class PageElementHandlerImpl extends AbstractElementHandlerImpl implements PageElementHandler {

	public PageElementHandlerImpl(BasicAbstractElementHandler basicElementHandler) {
		super(basicElementHandler);
	}

	@Override
	public <T extends AbstractPage> T getPageAs(Class<T> pageClass, Locator... locators) throws ElementTimeoutException {

		PageElement element = basicElementHandler.getElement(locators);

	}

	private <T extends AbstractPage> T newPage(Class<T> pageClass, AbstractPage page, PageElement pageElement) {
		try {
			Constructor<T> constructor = pageClass.getConstructor(AbstractPage.class, PageElement.class);
			boolean accessible = constructor.isAccessible();

			constructor.setAccessible(true);
			T newInstance = constructor.newInstance(page, pageElement);
			constructor.setAccessible(accessible);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
