package com.mgiorda.page;

import java.lang.reflect.Field;

public interface ElementFactory<T> {

	Object getValueForField(T element, Field field, Locator[] locators);
}
