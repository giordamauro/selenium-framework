package com.mgiorda.page.element;

import java.util.Collections;
import java.util.List;

import com.mgiorda.annotation.By;
import com.mgiorda.annotation.Locate;
import com.mgiorda.test.AbstractElement;

public class TableRow extends AbstractElement {

	@Locate(@By(tagName = "td"))
	private List<PageElement> dataValues;

	public TableRow(PageElement pageElement) {
		super(pageElement);
	}

	public List<PageElement> getDataValues() {
		return Collections.unmodifiableList(dataValues);
	}

	public PageElement getElementForColumn(int column) {
		return dataValues.get(column);
	}

	public <T extends AbstractElement> T getColumnAs(int column, Class<T> elementClass) {

		PageElement pageElement = getElementForColumn(column);
		T abstractElement = AbstractElement.factory(elementClass, elementHandler, pageElement);

		return abstractElement;
	}

	public int getColumnsSize() {
		return dataValues.size();
	}
}
