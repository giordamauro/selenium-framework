package com.mgiorda.page;

public interface ElementHandlerFactory {

	PageElementHandler getRootElementHandler();

	PageElementHandler getSubElementHandler(PageElementHandler elementHandler, PageElement parentElement);
}
