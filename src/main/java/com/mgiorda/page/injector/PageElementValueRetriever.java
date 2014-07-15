package com.mgiorda.page.injector;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.mgiorda.page.AbstractElementFactory;
import com.mgiorda.page.AbstractElementHandler;
import com.mgiorda.page.Locator;
import com.mgiorda.page.PageElement;

public class PageElementValueRetriever extends ElementValueRetriever implements ValueRetriever {

    public PageElementValueRetriever(AbstractElementHandler elementHandler) {
        super(elementHandler);
    }

    @Override
    public Object getValueForLocators(Field field, Locator[] locators) {

        Object value = null;

        Class<?> fieldClass = field.getType();
        AbstractElementFactory elementFactory = (AbstractElementFactory) elementHandler;

        if (PageElement.class.isAssignableFrom(fieldClass)) {

            value = elementFactory.getPageElement(locators);

        } else if (List.class.isAssignableFrom(fieldClass)) {

            ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
            Class<?> listClass = (Class<?>) fieldType.getActualTypeArguments()[0];

            if (PageElement.class.isAssignableFrom(listClass)) {

                value = elementFactory.getPageElements(locators);
            }
        }

        if (value == null) {
            value = super.getValueForLocators(field, locators);
        }

        return value;
    }

}
