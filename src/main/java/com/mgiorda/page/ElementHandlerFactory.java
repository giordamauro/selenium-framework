package com.mgiorda.page;

public interface ElementHandlerFactory {

	PageElementHandler getRootElementHandler();

	PageElementHandler getSubElementHandler(AbstractElementHandler elementHandler, PageElement parentElement);
}
