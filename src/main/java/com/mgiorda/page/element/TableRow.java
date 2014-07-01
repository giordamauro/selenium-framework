package com.mgiorda.page.element;

import java.util.Collections;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.AbstractElementFactory;
import com.mgiorda.page.PageElement;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;

public class TableRow extends AbstractElement {

	@Locate(@By(tagName = "td"))
	private List<PageElement> dataColumns;

	private TableHeaders headers;

	void setTableHeaders(TableHeaders headers) {
		this.headers = headers;
	}

	protected List<PageElement> getColumns() {
		return Collections.unmodifiableList(dataColumns);
	}

	protected PageElement getColumn(int column) {

		PageElement element = dataColumns.get(column);

		return element;
	}

	public <T> T getValueForHeaderAs(String headerName, Class<T> expectedClass) {

		int column = headers.getColumnForHeader(headerName);
		T value = getColumnAs(column, expectedClass);

		return value;
	}

	public <T extends AbstractElement> T getColumnAs(int column, Class<T> expectedClass) {

		PageElement pageElement = getColumn(column);

		AbstractElementFactory elmentFactory = (AbstractElementFactory) elementHandler;
		T value = elmentFactory.getElementAs(expectedClass, pageElement);

		return value;
	}

	public int getColumnsSize() {
		return dataColumns.size();
	}
}
