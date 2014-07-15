package com.mgiorda.page.elements.combobox;

public class ComboBox extends AbstractComboBox {

    protected void afterPropertiesSet() {

        verifyTagName("select");
        verifyAttributePresence("multiple", false);
    }

    public ComboBoxOption getSelectedOption() {

        for (ComboBoxOption option : options) {
            if (option.isSelected()) {
                return option;
            }
        }
        throw new IllegalStateException("There isn't any option selected");
    }

    public String getSelectedValue() {

        ComboBoxOption selectedOption = getSelectedOption();
        String value = selectedOption.getValue();

        return value;
    }

    public String getSelectedText() {

        ComboBoxOption selectedOption = getSelectedOption();
        String value = selectedOption.getText();

        return value;
    }
}
