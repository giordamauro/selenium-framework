package com.mgiorda.page.element;

import java.util.Collections;
import java.util.List;

import com.mgiorda.annotation.By;
import com.mgiorda.annotation.Locate;
import com.mgiorda.test.AbstractElement;

public class TableHeaders extends AbstractElement {

	@Locate(@By(tagName = "th"))
	private List<PageElement> headers;

	public TableHeaders(PageElement pageElement) {
		super(pageElement);
	}

	public <T extends AbstractElement> T getColumnAs(int column, Class<T> elementClass) {

		PageElement pageElement = getElementForColumn(column);
		T abstractElement = AbstractElement.factory(elementClass, elementHandler, pageElement);

		return abstractElement;
	}

	public int getColumnsSize() {
		return headers.size();
	}

	int getColumnForHeader(String headerName) {

		int headerColumn = -1;

		int i = 0;
		while (i < getColumnsSize() && headerColumn == -1) {

			Label header = getColumnAs(i, Label.class);
			if (headerName.equalsIgnoreCase(header.getText())) {
				headerColumn = i;
			} else {
				i++;
			}
		}
		if (headerColumn == -1) {
			throw new IllegalStateException(String.format("Couln't find table header named '%s'", headerName));
		}

		return i;
	}

	protected List<PageElement> getHeaders() {
		return Collections.unmodifiableList(headers);
	}

	protected PageElement getElementForColumn(int column) {
		return headers.get(column);
	}
}
