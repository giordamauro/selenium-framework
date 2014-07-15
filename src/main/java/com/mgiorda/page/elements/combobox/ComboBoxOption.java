package com.mgiorda.page.elements.combobox;

import com.mgiorda.page.AbstractElement;

public class ComboBoxOption extends AbstractElement {

    public void afterPropertiesSet() {

        verifyTagName("option");
    }

    public String getValue() {

        String value = pageElement.getAttribute("value");

        return value;
    }

    public String getText() {

        String text = pageElement.getText();

        return text;
    }

    public void setSelected(boolean value) {

        logger.info(String.format("Setting option value '%s', text '%s' selected: '%s'", getValue(), getText(), value));

        if ((value && !isSelected()) || (!value && isSelected())) {
            pageElement.click();
        }
    }

    public boolean isSelected() {
        boolean selected = pageElement.isSelected();

        return selected;
    }
}
