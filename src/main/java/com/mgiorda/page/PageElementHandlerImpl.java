package com.mgiorda.page;

import java.lang.reflect.Constructor;

public class PageElementHandlerImpl extends AbstractElementHandlerImpl implements PageElementHandler {

	private AbstractPage page;

	public PageElementHandlerImpl(BasicAbstractElementHandler basicElementHandler, AbstractPage page) {
		super(basicElementHandler);
		this.page = page;
	}

	private PageElementHandlerImpl(BasicAbstractElementHandler basicElementHandler) {
		super(basicElementHandler);
	}

	@Override
	public <T extends AbstractPage> T getPageAs(Class<T> pageClass, Locator... locators) throws ElementTimeoutException {

		PageElement element = basicElementHandler.getElement(locators);

		BasicAbstractElementHandler basicHandler = new BasicAbstractElementHandler(basicElementHandler, element);
		PageElementHandlerImpl elementHandler = new PageElementHandlerImpl(basicHandler);

		T subPage = newPage(pageClass, elementHandler);

		return subPage;
	}

	private <T extends AbstractPage> T newPage(Class<T> pageClass, PageElementHandlerImpl elementHandler) {
		try {
			Constructor<T> constructor = pageClass.getConstructor(AbstractPage.class, PageElementHandler.class);
			boolean accessible = constructor.isAccessible();

			constructor.setAccessible(true);
			T newInstance = constructor.newInstance(page, elementHandler);
			constructor.setAccessible(accessible);

			elementHandler.page = newInstance;

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
