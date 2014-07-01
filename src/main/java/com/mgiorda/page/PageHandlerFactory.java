package com.mgiorda.page;

public interface PageHandlerFactory {

	PageElementHandler getElementHandler(AbstractPage page);

	DriverActionHandler getActionHandler();
}
