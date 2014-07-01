package com.mgiorda.page;

import com.mgiorda.page.support.ValueRetriever;

public interface ElementInjector {

	void autowireLocators(ValueRetriever valueRetriever, Object target);
}
