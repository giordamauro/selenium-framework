package com.mgiorda.page.elements;

import com.mgiorda.page.AbstractElement;

public class Checkbox extends AbstractElement {

    protected void afterPropertiesSet() {
        verifyTagName("input");
    }

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
