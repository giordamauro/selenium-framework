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

	public List<PageElement> getHeaders() {
		return Collections.unmodifiableList(headers);
	}

	public PageElement getElementForColumn(int column) {
		return headers.get(column);
	}

	public <T extends AbstractElement> T getColumnAs(int column, Class<T> elementClass) {

		PageElement pageElement = getElementForColumn(column);
		T abstractElement = AbstractElement.factory(elementClass, elementHandler, pageElement);

		return abstractElement;
	}

	public int getColumnsSize() {
		return headers.size();
	}
}
