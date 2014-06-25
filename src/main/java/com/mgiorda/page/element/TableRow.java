package com.mgiorda.page.element;

import java.util.Collections;
import java.util.List;

import com.mgiorda.annotation.By;
import com.mgiorda.annotation.Locate;
import com.mgiorda.test.AbstractElement;

public class TableRow extends AbstractElement {

	@Locate(@By(tagName = "td"))
	private List<PageElement> dataColumns;

	public TableRow(PageElement pageElement) {
		super(pageElement);
	}

	protected List<PageElement> getColumns() {
		return Collections.unmodifiableList(dataColumns);
	}

	protected PageElement getColumn(int column) {
		return dataColumns.get(column);
	}

	public <T extends AbstractElement> T getColumnAs(int column, Class<T> elementClass) {

		PageElement pageElement = getColumn(column);
		T abstractElement = AbstractElement.factory(elementClass, elementHandler, pageElement);

		return abstractElement;
	}

	public int getColumnsSize() {
		return dataColumns.size();
	}
}
