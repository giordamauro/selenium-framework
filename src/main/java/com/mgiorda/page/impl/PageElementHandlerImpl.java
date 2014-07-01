package com.mgiorda.page.impl;

import java.lang.reflect.Constructor;

import com.mgiorda.page.AbstractPage;
import com.mgiorda.page.ElementTimeoutException;
import com.mgiorda.page.Locator;
import com.mgiorda.page.PageElement;
import com.mgiorda.page.PageElementHandler;

public class PageElementHandlerImpl extends AbstractElementHandlerImpl implements PageElementHandler {

	private AbstractPage page;

	public PageElementHandlerImpl(DriverElementHandler driverElementHandler, AbstractPage page) {
		super(driverElementHandler);

		this.page = page;
	}

	private PageElementHandlerImpl(DriverElementHandler basicElementHandler) {
		super(basicElementHandler);
	}

	@Override
	public <T extends AbstractPage> T getPageAs(Class<T> pageClass, Locator... locators) throws ElementTimeoutException {

		PageElement element = driverElementHandler.getElement(locators);

		DriverElementHandler basicHandler = new DriverElementHandler(driverElementHandler, element);
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
