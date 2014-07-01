package com.mgiorda.page.element;

import java.util.Collections;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.PageElement;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;

public class TableHeaders extends AbstractElement {

	@Locate(@By(tagName = "th"))
	private List<PageElement> headers;

	public <T extends AbstractElement> T getColumnAs(int column, Class<T> elementClass) {

		PageElement pageElement = getElementForColumn(column);
		T abstractElement = elementHandler.getElementAs(elementClass, pageElement);

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
