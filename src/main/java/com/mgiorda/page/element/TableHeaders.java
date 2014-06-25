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

	public PageElement getHeaderForColumn(int column) {
		return headers.get(column);
	}

	public int getColumnsSize() {
		return headers.size();
	}
}
