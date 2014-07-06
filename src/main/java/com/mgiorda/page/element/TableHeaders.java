package com.mgiorda.page.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.AbstractElementFactory;
import com.mgiorda.page.PageElement;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;

public class TableHeaders extends AbstractElement {

	@Locate(@By(tagName = "th"))
	private List<PageElement> headers;

	public List<String> getColumns() {

		List<String> values = new ArrayList<>();
		for (int i = 0; i < headers.size(); i++) {

			String value = getColumn(i);
			values.add(value);
		}

		return values;
	}

	public String getColumn(int column) {

		PageElement element = getColumnPageElement(column);
		String value = element.getText();

		return value;
	}

	public <T extends AbstractElement> T getColumn(int column, Class<T> elementClass) {

		PageElement element = getColumnPageElement(column);
		AbstractElementFactory elementFactory = (AbstractElementFactory) elementHandler;

		T value = elementFactory.adaptPageElementAs(elementClass, element);

		return value;
	}

	public int getColumnsSize() {
		return headers.size();
	}

	int getIndexNamed(String headerName) {

		int headerColumn = -1;

		int i = 0;
		while (i < getColumnsSize() && headerColumn == -1) {

			String header = getColumn(i);
			if (headerName.equalsIgnoreCase(header)) {
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

	protected PageElement getColumnPageElement(int column) {
		return headers.get(column);
	}
}
