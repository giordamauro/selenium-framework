package com.mgiorda.page;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.mgiorda.page.element.Label;
import com.mgiorda.test.AbstractElement;

public class AbstractElementHandlerImpl implements AbstractElementHandler {

	protected final BasicAbstractElementHandler basicElementHandler;

	public AbstractElementHandlerImpl(BasicAbstractElementHandler basicElementHandler) {
		this.basicElementHandler = basicElementHandler;
	}

	@Override
	public boolean existsElement(Locator... locators) {
		return basicElementHandler.existsElement(locators);
	}

	@Override
	public int getElementCount(Locator... locators) {
		return basicElementHandler.getElementCount(locators);
	}

	@Override
	public <T extends AbstractElement> T getElementAs(Class<T> elementClass, Locator... locators) throws ElementTimeoutException {

		PageElement element = basicElementHandler.getElement(locators);
		T abstractElement = newAbstractElement(elementClass, element);

		return abstractElement;
	}

	@Override
	public <T extends AbstractElement> List<T> getElementsAs(Class<T> elementClass, Locator... locators) throws ElementTimeoutException {

		List<T> list = new ArrayList<T>();

		List<PageElement> elements = basicElementHandler.getElements(locators);
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

		BasicAbstractElementHandler basicHandler = new BasicAbstractElementHandler(basicElementHandler, pageElement);
		AbstractElementHandler elementHandler = new AbstractElementHandlerImpl(basicHandler);

		T abstractElement = newAbstractElement(elementClass, elementHandler, pageElement);

		return abstractElement;
	}

	private <T extends AbstractElement> T newAbstractElement(Class<T> elementClass, AbstractElementHandler elementHandler, PageElement pageElement) {
		try {
			Constructor<T> constructor = elementClass.getConstructor(AbstractElementHandler.class, PageElement.class);
			boolean accessible = constructor.isAccessible();

			constructor.setAccessible(true);
			T newInstance = constructor.newInstance(elementHandler, pageElement);
			constructor.setAccessible(accessible);

			return newInstance;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
