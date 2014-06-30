package com.mgiorda.page;

public interface AbstractElementHandlerFactory {

	AbstractElementHandler getHandlerForPage(AbstractPage page);

	AbstractElementHandler getHandlerForElement(AbstractElementHandler elementHandler, PageElement parentElement);

}
