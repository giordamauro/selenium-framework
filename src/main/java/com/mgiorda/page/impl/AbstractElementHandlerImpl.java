package com.mgiorda.page.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.AbstractElementFactory;
import com.mgiorda.page.AbstractElementHandler;
import com.mgiorda.page.ElementInjector;
import com.mgiorda.page.ElementTimeoutException;
import com.mgiorda.page.Locator;
import com.mgiorda.page.PageElement;
import com.mgiorda.page.elements.Label;
import com.mgiorda.page.injector.PageElementValueRetriever;
import com.mgiorda.page.injector.ValueRetriever;

public class AbstractElementHandlerImpl implements AbstractElementHandler, AbstractElementFactory {

	private final ElementInjector elementInjector;
	protected final DriverElementHandler driverElementHandler;

	public AbstractElementHandlerImpl(DriverElementHandler basicElementHandler, ElementInjector elementInjector) {
		this.driverElementHandler = basicElementHandler;
		this.elementInjector = elementInjector;
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
		T abstractElement = adaptPageElementAs(elementClass, element);

		return abstractElement;
	}

	@Override
	public <T extends AbstractElement> List<T> getElementsAs(Class<T> elementClass, Locator... locators) throws ElementTimeoutException {

		List<T> list = new ArrayList<T>();

		List<PageElement> elements = driverElementHandler.getElements(locators);
		for (PageElement pageElement : elements) {

			T element = adaptPageElementAs(elementClass, pageElement);
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

	public <T extends AbstractElement> T adaptPageElementAs(Class<T> elementClass, PageElement pageElement) {

		AbstractElementHandler elementHandler = newElementHandler(pageElement);
		T abstractElement = newAbstractElement(elementClass, elementHandler, pageElement);

		return abstractElement;
	}

	public AbstractElementFactory newElementFactory(PageElement pageElement) {

		AbstractElementFactory elementFactory = (AbstractElementFactory) newElementHandler(pageElement);

		return elementFactory;
	}

	private AbstractElementHandler newElementHandler(PageElement pageElement) {

		DriverElementHandler basicHandler = new DriverElementHandler(driverElementHandler, pageElement);
		AbstractElementHandlerImpl elementHandler = new AbstractElementHandlerImpl(basicHandler, elementInjector);

		return elementHandler;
	}

	private <T extends AbstractElement> T newAbstractElement(Class<T> elementClass, AbstractElementHandler elementHandler, PageElement pageElement) {
		try {
			T newInstance = elementClass.newInstance();

			Method method = AbstractElement.class.getDeclaredMethod("setAbstractElement", AbstractElementHandler.class, ElementInjector.class, PageElement.class);

			boolean isAccessible = method.isAccessible();

			method.setAccessible(true);
			method.invoke(newInstance, elementHandler, elementInjector, pageElement);
			method.setAccessible(isAccessible);

			ValueRetriever elementValueRetriever = new PageElementValueRetriever(elementHandler);
			elementInjector.autowireLocators(elementValueRetriever, newInstance);

			callAfterPropertiesSet(newInstance);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private <T extends AbstractElement> void callAfterPropertiesSet(T element) {

		Class<? extends AbstractElement> elementClass = element.getClass();
		Method afterProperties = null;

		while (elementClass != null && afterProperties == null) {
			try {
				afterProperties = elementClass.getDeclaredMethod("afterPropertiesSet");
			} catch (NoSuchMethodException e) {

				@SuppressWarnings("unchecked")
				Class<? extends AbstractElement> superClass = (Class<? extends AbstractElement>) elementClass.getSuperclass();
				elementClass = superClass;
			}
		}
		if (afterProperties != null) {

			try {
				boolean methodAccessible = afterProperties.isAccessible();
				afterProperties.setAccessible(true);

				afterProperties.invoke(element);
				afterProperties.setAccessible(methodAccessible);

			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

	}

	@Override
	public PageElement getPageElement(Locator... locators) throws ElementTimeoutException {

		PageElement element = driverElementHandler.getElement(locators);

		return element;
	}

	@Override
	public List<PageElement> getPageElements(Locator... locators) throws ElementTimeoutException {

		List<PageElement> elements = driverElementHandler.getElements(locators);

		return elements;
	}
}
