package com.mgiorda.page.elements.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;

public class Table extends AbstractElement implements Iterable<TableRow> {

	@Locate(@By(xpath = "(tr[th]) | (thead/tr[th])"))
	private TableHeaders headers;

	@Locate(@By(xpath = "(tr[td]) | (tbody/tr[td])"))
	private List<TableRow> rows;

	void afterPropertiesSet() {

		if (headers != null) {

			for (TableRow row : rows) {
				row.setTableHeaders(headers);
			}
		}
	}

	public TableHeaders getHeaders() {
		return headers;
	}

	public List<TableRow> getRows() {
		return rows;
	}

	public List<String> getColumnValues(int column) {

		List<String> values = new ArrayList<>();

		for (TableRow row : rows) {
			String value = row.getValue(column);
			values.add(value);
		}

		return values;
	}

	public List<String> getColumnValues(String columnName) {

		if (headers == null) {
			throw new IllegalStateException(String.format("Cannot get column values for columnName '%s': table headers are not defined", columnName));
		}

		int column = headers.getIndexNamed(columnName);

		List<String> values = this.getColumnValues(column);

		return values;
	}

	public TableRow getRow(String columnName, String value) {

		TableRow row = null;

		List<String> columnValues = getColumnValues(columnName);

		int column = -1;
		int i = 0;
		while (i < columnValues.size() && column == -1) {
			String columValue = columnValues.get(i);
			if (columValue.equals(value)) {
				column = i;
			} else {
				i++;
			}
		}
		if (column != -1) {
			row = rows.get(column);
		}

		return row;
	}

	public Iterator<TableRow> iterator() {
		return rows.iterator();
	}
}
