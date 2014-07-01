package com.mgiorda.page;

import com.mgiorda.page.injector.ValueRetriever;

public interface ElementInjector {

	void autowireLocators(ValueRetriever valueRetriever, Object target);
}
