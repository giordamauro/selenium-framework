package com.mgiorda.page.elements.combobox;

import java.util.Collections;
import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;

public abstract class AbstractComboBox extends AbstractElement {

	@Locate(@By(tagName = "option"))
	protected List<ComboBoxOption> options;

	public List<ComboBoxOption> getOptions() {

		return Collections.unmodifiableList(options);
	}

	public boolean existsOptionByValue(String optionValue) {

		ComboBoxOption option = getByValue(optionValue);

		return option != null;
	}

	public boolean existsOptionByText(String optionText) {

		ComboBoxOption option = getByText(optionText);

		return option != null;
	}

	public ComboBoxOption getOptionByValue(String optionValue) {

		ComboBoxOption option = getByValue(optionValue);
		if (option == null) {
			throw new IllegalStateException(String.format("Option with value '%s' does not exist in ComboBox", optionValue));
		}

		return option;
	}

	public ComboBoxOption getOptionByText(String optionText) {

		ComboBoxOption option = getByText(optionText);
		if (option == null) {
			throw new IllegalStateException(String.format("Option with text '%s' does not exist in ComboBox", optionText));
		}

		return option;
	}

	public void selectValue(String optionValue) {

		ComboBoxOption option = getOptionByValue(optionValue);
		option.setSelected(true);
	}

	public void selectText(String optionText) {

		ComboBoxOption option = getOptionByText(optionText);
		option.setSelected(true);
	}

	protected ComboBoxOption getByValue(String optionValue) {

		for (ComboBoxOption option : options) {
			if (option.getValue().equalsIgnoreCase(optionValue)) {
				return option;
			}
		}
		return null;
	}

	protected ComboBoxOption getByText(String optionText) {

		for (ComboBoxOption option : options) {
			if (option.getText().equalsIgnoreCase(optionText)) {
				return option;
			}
		}
		return null;
	}
}
