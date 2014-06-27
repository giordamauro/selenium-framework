package com.mgiorda.page.element;

import com.mgiorda.test.AbstractElement;

public class Checkbox extends AbstractElement {

	public Checkbox(PageElement pageElement) {
		super(pageElement);
	}

	public void setSelected(boolean value) {

		if ((value && !isSelected()) || (!value && isSelected())) {
			pageElement.click();
		}
	}

	public boolean isSelected() {
		boolean selected = pageElement.isSelected();

		return selected;
	}
}
