package com.mgiorda.page.element;

import com.mgiorda.page.AbstractElement;

public class Checkbox extends AbstractElement {

	public void setSelected(boolean value) {

		logger.info(String.format("Setting selected: '%s' in element", value) + getNameInfo());

		if ((value && !isSelected()) || (!value && isSelected())) {
			pageElement.click();
		}
	}

	public boolean isSelected() {
		boolean selected = pageElement.isSelected();

		return selected;
	}
}
