package com.mgiorda.page.element;

import java.util.ArrayList;
import java.util.List;

import com.mgiorda.annotation.By;
import com.mgiorda.annotation.Locate;
import com.mgiorda.test.AbstractElement;

public class Table extends AbstractElement {

	@Locate(@By(xpath = "(tr[th]) | (thead/tr[th])"))
	private TableHeaders headers;

	@Locate(@By(xpath = "(tr[td]) | (tbody/tr[td])"))
	private List<TableRow> rows;

	public Table(PageElement pageElement) {
		super(pageElement);
	}

	public TableHeaders getHeaders() {
		return headers;
	}

	public List<TableRow> getRows() {
		return rows;
	}

	public List<String> getValuesForColumnAs(int column) {

		List<String> values = getValuesForColumnAs(column, String.class);

		return values;
	}

	public <T> List<T> getValuesForColumnAs(int column, Class<T> elementClass) {

		List<T> elements = new ArrayList<>();

		for (TableRow row : rows) {
			T element = row.getColumnAs(column, elementClass);
			elements.add(element);
		}

		return elements;
	}

	public List<String> getValuesForHeader(String headerName) {

		List<String> values = getValuesForHeaderAs(headerName, String.class);

		return values;
	}

	public <T> List<T> getValuesForHeaderAs(String headerName, Class<T> elementClass) {

		int headerColumn = headers.getColumnForHeader(headerName);
		List<T> columnRows = getValuesForColumnAs(headerColumn, elementClass);

		return columnRows;
	}

	protected List<PageElement> getRowsForHeader(String headerName) {

		int headerColumn = headers.getColumnForHeader(headerName);
		List<PageElement> columnRows = getColumnRows(headerColumn);

		return columnRows;
	}

	protected List<PageElement> getColumnRows(int column) {

		List<PageElement> elements = new ArrayList<>();

		for (TableRow row : rows) {
			PageElement element = row.getColumn(column);
			elements.add(element);
		}

		return elements;
	}

}
