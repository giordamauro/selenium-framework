package com.mgiorda.page.injector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.ElementInjector;
import com.mgiorda.page.Locator;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;
import com.mgiorda.page.elements.Label;

public class ElementInjectorImpl implements ElementInjector {

    private static final Log logger = LogFactory.getLog(ElementInjector.class);

    public void autowireLocators(ValueRetriever valueRetriever, Object target) {

        Class<?> objClass = target.getClass();

        while (objClass != null) {
            autowireLocators(valueRetriever, target, objClass);
            objClass = objClass.getSuperclass();
        }
    }

    private void autowireLocators(ValueRetriever valueRetriever, Object target, Class<?> objClass) {

        Field[] declaredFields = objClass.getDeclaredFields();
        for (Field field : declaredFields) {

            Locate annotation = field.getAnnotation(Locate.class);
            if (annotation != null && annotation.fetchOnInit()) {

                Locator[] locators = getLocatorsFromAnnotation(annotation);
                if (locators.length == 0) {
                    throw new IllegalStateException(String.format("Couldn't find an element locator for field '%s' in page class '%s'", field.getName(), objClass.getSimpleName()));
                }

                Object value = valueRetriever.getValueForLocators(field, locators);

                if (value == null) {
                    throw new IllegalStateException(String.format("Cannot autowire field '%s' of type '%s' in '%s'", field.getName(), field.getType(), objClass));
                }

                setField(target, field, value);

                if (AbstractElement.class.isAssignableFrom(value.getClass())) {

                    AbstractElement element = (AbstractElement) value;
                    setAbstractElementFieldName(element, field.getName(), objClass);
                }
            }
        }
    }

    public void autowireField(ValueRetriever valueRetriever, Object target, String fieldName) {

        Class<?> objClass = target.getClass();

        try {

            Field field = objClass.getDeclaredField(fieldName);

            Locate annotation = field.getAnnotation(Locate.class);
            if (annotation == null) {
                throw new IllegalStateException(String.format("Couldn't find @Locate annotation for field '%s' in page class '%s'", field.getName(), objClass.getSimpleName()));
            }

            Locator[] locators = getLocatorsFromAnnotation(annotation);
            if (locators.length == 0) {
                throw new IllegalStateException(String.format("Couldn't find an element locator for field '%s' in page class '%s'", field.getName(), objClass.getSimpleName()));
            }

            Object value = valueRetriever.getValueForLocators(field, locators);

            if (value == null) {
                throw new IllegalStateException(String.format("Cannot autowire field '%s' of type '%s' in '%s'", field.getName(), field.getType(), objClass));
            }

            setField(target, field, value);

            if (AbstractElement.class.isAssignableFrom(value.getClass())) {

                AbstractElement element = (AbstractElement) value;
                setAbstractElementFieldName(element, field.getName(), objClass);
            }

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Locator[] getLocatorsFromAnnotation(Locate annotation) {

        List<Locator> locators = new ArrayList<Locator>();
        By[] multipleLocators = annotation.value();

        for (By byAnnotation : multipleLocators) {
            Locator locator = getByAnnotation(byAnnotation);
            if (locator != null) {
                locators.add(locator);
            }
        }

        Locator[] locatorsArray = locators.toArray(new Locator[] {});

        return locatorsArray;
    }

    private Locator getByAnnotation(By annotation) {

        Locator elementLocator = null;

        String id = annotation.id();
        String name = annotation.name();
        String css = annotation.css();
        String className = annotation.className();
        String tagName = annotation.tagName();
        String linkText = annotation.linkText();
        String partialLinkText = annotation.partialLinkText();
        String xpath = annotation.xpath();

        if (!id.equals("")) {
            elementLocator = Locator.byId(id);
        } else if (!name.equals("")) {
            elementLocator = Locator.byName(name);
        } else if (!css.equals("")) {
            elementLocator = Locator.byCssSelector(css);
        } else if (!className.equals("")) {
            elementLocator = Locator.byClass(className);
        } else if (!tagName.equals("")) {
            elementLocator = Locator.byTagName(tagName);
        } else if (!linkText.equals("")) {
            elementLocator = Locator.byLinkText(linkText);
        } else if (!partialLinkText.equals("")) {
            elementLocator = Locator.byPartialLinkText(partialLinkText);
        } else if (!xpath.equals("")) {
            elementLocator = Locator.byXpath(xpath);
        }

        return elementLocator;
    }

    private void setField(Object target, Field field, Object value) {

        Class<?> fieldClass = field.getType();
        Class<?> valueClass = value.getClass();

        if (String.class.isAssignableFrom(fieldClass) && Label.class.isAssignableFrom(valueClass)) {
            Label labelValue = (Label) value;
            value = labelValue.getText();
        }

        boolean isFieldAccessible = field.isAccessible();

        field.setAccessible(true);
        try {

            logger.debug(String.format("Setting '%s' field in '%s' class - Value: %s", field.getName(), target.getClass().getSimpleName(), value));
            field.set(target, value);

        } catch (IllegalArgumentException | IllegalAccessException e) {

            throw new IllegalStateException(e);
        }
        field.setAccessible(isFieldAccessible);
    }

    private void setAbstractElementFieldName(AbstractElement element, String fieldName, Class<?> objClass) {

        try {
            Method method = AbstractElement.class.getDeclaredMethod("setFieldName", Class.class, String.class);

            boolean isAccessible = method.isAccessible();
            method.setAccessible(true);

            logger.debug(String.format("Setting '%s' elementName for '%s' class", fieldName, element.getClass()));
            method.invoke(element, objClass, fieldName);

            method.setAccessible(isAccessible);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
