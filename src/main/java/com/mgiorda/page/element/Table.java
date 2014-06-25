package com.mgiorda.page.element;

import java.util.List;

import com.mgiorda.annotation.By;
import com.mgiorda.annotation.Locate;
import com.mgiorda.test.AbstractElement;

public class Table extends AbstractElement {

	@Locate(@By(xpath = "tr[th]"))
	private TableHeaders headers;

	@Locate(@By(xpath = "tr[td]"))
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
}
