package com.mgiorda.page.elements.combobox;

import java.util.ArrayList;
import java.util.List;

public class MultipleComboBox extends AbstractComboBox {

    protected void afterPropertiesSet() {

        verifyTagName("select");
        verifyAttributePresence("multiple", true);
    }

    public List<ComboBoxOption> getSelectedOptions() {

        List<ComboBoxOption> selected = new ArrayList<>();

        for (ComboBoxOption option : options) {
            if (option.isSelected()) {
                selected.add(option);
            }
        }
        return selected;
    }

    public List<String> getSelectedValues() {

        List<String> values = new ArrayList<>();

        List<ComboBoxOption> selectedOptions = getSelectedOptions();
        for (ComboBoxOption selectedOption : selectedOptions) {
            String value = selectedOption.getValue();
            values.add(value);
        }

        return values;
    }

    public List<String> getSelectedTexts() {

        List<String> texts = new ArrayList<>();

        List<ComboBoxOption> selectedOptions = getSelectedOptions();
        for (ComboBoxOption selectedOption : selectedOptions) {
            String text = selectedOption.getText();
            texts.add(text);
        }

        return texts;
    }

    public void deselectValue(String optionValue) {

        ComboBoxOption option = getOptionByValue(optionValue);
        option.setSelected(false);
    }

    public void deselectText(String optionText) {

        ComboBoxOption option = getOptionByText(optionText);
        option.setSelected(false);
    }

    public void deselectAll() {

        List<ComboBoxOption> selectedOptions = getSelectedOptions();
        for (ComboBoxOption selectedOption : selectedOptions) {

            selectedOption.setSelected(false);
        }
    }
}
