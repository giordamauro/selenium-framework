package com.mgiorda.page.support;

import java.lang.reflect.Field;

import com.mgiorda.page.AbstractElementHandler;
import com.mgiorda.page.AbstractPage;
import com.mgiorda.page.Locator;
import com.mgiorda.page.PageElementHandler;

public class PageValueRetriever implements ValueRetriever {

	private final PageElementHandler pageHandler;

	public PageValueRetriever(PageElementHandler pageHandler) {
		this.pageHandler = pageHandler;
	}

	@Override
	public Object getValueForLocators(Field field, Locator[] locators) {

		Object value = getPageForField(field, locators);

		if (value == null) {
			AbstractElementHandler elementHandler = (AbstractElementHandler) pageHandler;

			ValueRetriever elementRetriever = new ElementValueRetriever(elementHandler);
			value = elementRetriever.getValueForLocators(field, locators);
		}

		return value;
	}

	private AbstractPage getPageForField(Field field, Locator[] locators) {

		AbstractPage page = null;

		Class<?> fieldType = field.getType();

		if (AbstractPage.class.isAssignableFrom(fieldType)) {

			@SuppressWarnings("unchecked")
			Class<? extends AbstractPage> pageClass = (Class<? extends AbstractPage>) fieldType;

			page = pageHandler.getPageAs(pageClass, locators);
		}

		return page;
	}

}
