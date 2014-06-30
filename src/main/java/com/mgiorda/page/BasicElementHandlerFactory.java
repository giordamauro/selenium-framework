package com.mgiorda.page;

public interface BasicElementHandlerFactory {

	BasicAbstractElementHandler getElementHandler(BasicAbstractElementHandler elementHandler, PageElement element);
}
