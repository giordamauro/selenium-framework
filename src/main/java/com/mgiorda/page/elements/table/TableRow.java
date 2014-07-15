package com.mgiorda.page.elements.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.AbstractElementFactory;
import com.mgiorda.page.Locator;
import com.mgiorda.page.PageElement;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;
import com.mgiorda.page.elements.Link;
import com.mgiorda.page.elements.TextField;

public class TableRow extends AbstractElement {

    @Locate(@By(tagName = "td"))
    private List<PageElement> dataColumns;

    private TableHeaders headers;

    public List<String> getValues() {

        List<String> values = new ArrayList<>();
        for (int i = 0; i < dataColumns.size(); i++) {

            String value = getValue(i);
            values.add(value);
        }

        return values;
    }

    public String getValue(int column) {

        PageElement element = getPageElement(column);
        String value = element.getText();

        return value;
    }

    public String getValue(String columnName) {

        PageElement element = getPageElement(columnName);
        String value = element.getText();

        return value;
    }

    public <T extends AbstractElement> T getValue(int column, Class<T> elementClass) {

        PageElement element = this.getPageElement(column);
        AbstractElementFactory elmentFactory = (AbstractElementFactory) elementHandler;

        T value = elmentFactory.adaptPageElementAs(elementClass, element);

        return value;
    }

    public <T extends AbstractElement> T getValue(String columnName, Class<T> elementClass) {

        PageElement element = getPageElement(columnName);
        AbstractElementFactory elmentFactory = (AbstractElementFactory) elementHandler;

        T value = elmentFactory.adaptPageElementAs(elementClass, element);

        return value;
    }

    public Link getInnerLink(int column) {

        PageElement element = this.getPageElement(column);
        Link link = adaptInnerElement(element, Link.class, Locator.byTagName("a"));

        return link;
    }

    public Link getInnerLink(String columnName) {

        PageElement element = getPageElement(columnName);
        Link link = adaptInnerElement(element, Link.class, Locator.byTagName("a"));

        return link;
    }

    public TextField getInnerTextField(int column) {

        PageElement element = this.getPageElement(column);
        TextField textField = adaptInnerElement(element, TextField.class, Locator.byXpath("input[@type = \"text\"]"));

        return textField;
    }

    public TextField getInnerTextField(String columnName) {

        PageElement element = getPageElement(columnName);
        TextField textField = adaptInnerElement(element, TextField.class, Locator.byXpath("input[@type = \"text\"]"));

        return textField;
    }

    public int getColumnsSize() {
        return dataColumns.size();
    }

    void setTableHeaders(TableHeaders headers) {
        this.headers = headers;
    }

    protected List<PageElement> getColumns() {
        return Collections.unmodifiableList(dataColumns);
    }

    protected PageElement getPageElement(int column) {

        PageElement element = dataColumns.get(column);

        return element;
    }

    protected PageElement getPageElement(String columnName) {

        if (headers == null) {
            throw new IllegalStateException(String.format("Cannot get value for columnName '%s': table headers are not defined", columnName));
        }

        int column = headers.getIndexNamed(columnName);

        PageElement element = this.getPageElement(column);

        return element;
    }

    private <T extends AbstractElement> T adaptInnerElement(PageElement parentElement, Class<T> elementClass, Locator... locators) {

        AbstractElementFactory elementFactory = (AbstractElementFactory) elementHandler;
        AbstractElementFactory subElementFactory = elementFactory.newElementFactory(parentElement);

        PageElement innerElement = subElementFactory.getPageElement(locators);
        T inner = subElementFactory.adaptPageElementAs(elementClass, innerElement);

        return inner;
    }

}
