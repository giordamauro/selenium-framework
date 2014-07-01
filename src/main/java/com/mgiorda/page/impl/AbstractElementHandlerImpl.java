package com.mgiorda.page.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.AbstractElementHandler;
import com.mgiorda.page.ElementTimeoutException;
import com.mgiorda.page.Locator;
import com.mgiorda.page.PageElement;
import com.mgiorda.page.element.Label;

public class AbstractElementHandlerImpl implements AbstractElementHandler {

	protected final DriverElementHandler driverElementHandler;

	public AbstractElementHandlerImpl(DriverElementHandler basicElementHandler) {
		this.driverElementHandler = basicElementHandler;
	}

	@Override
	public boolean existsElement(Locator... locators) {
		return driverElementHandler.existsElement(locators);
	}

	@Override
	public int getElementCount(Locator... locators) {
		return driverElementHandler.getElementCount(locators);
	}

	@Override
	public <T extends AbstractElement> T getElementAs(Class<T> elementClass, Locator... locators) throws ElementTimeoutException {

		PageElement element = driverElementHandler.getElement(locators);
		T abstractElement = newAbstractElement(elementClass, element);

		return abstractElement;
	}

	@Override
	public <T extends AbstractElement> List<T> getElementsAs(Class<T> elementClass, Locator... locators) throws ElementTimeoutException {

		List<T> list = new ArrayList<T>();

		List<PageElement> elements = driverElementHandler.getElements(locators);
		for (PageElement pageElement : elements) {

			T element = newAbstractElement(elementClass, pageElement);
			list.add(element);
		}
		return list;
	}

	@Override
	public String getElement(Locator... locators) throws ElementTimeoutException {

		Label label = getElementAs(Label.class, locators);
		String text = label.getText();

		return text;
	}

	@Override
	public List<String> getElements(Locator... locators) throws ElementTimeoutException {

		List<String> list = new ArrayList<>();

		List<Label> labels = getElementsAs(Label.class, locators);
		for (Label label : labels) {
			list.add(label.getText());
		}

		return list;
	}

	private <T extends AbstractElement> T newAbstractElement(Class<T> elementClass, PageElement pageElement) {

		DriverElementHandler basicHandler = new DriverElementHandler(driverElementHandler, pageElement);
		AbstractElementHandler elementHandler = new AbstractElementHandlerImpl(basicHandler);

		T abstractElement = newAbstractElement(elementClass, elementHandler, pageElement);

		return abstractElement;
	}

	private <T extends AbstractElement> T newAbstractElement(Class<T> elementClass, AbstractElementHandler elementHandler, PageElement pageElement) {
		try {
			T newInstance = elementClass.newInstance();

			Method method = AbstractElement.class.getDeclaredMethod("setAbstractElement", AbstractElementHandler.class, PageElement.class);

			boolean isAccessible = method.isAccessible();

			method.setAccessible(true);
			method.invoke(newInstance, elementHandler, pageElement);
			method.setAccessible(isAccessible);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
