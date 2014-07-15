package com.mgiorda.page.elements;

import com.mgiorda.page.AbstractElement;

public class Link extends AbstractElement {

    protected void afterPropertiesSet() {
        verifyTagName("a");
    }

    public void click() {

        logger.info("Clicking on element" + getNameInfo());

        pageElement.click();
    }

    public void click(long afterTimeMillis) {

        logger.info("Clicking on element" + getNameInfo());

        pageElement.click(afterTimeMillis);
    }

    public String getLinkText() {
        String text = pageElement.getText();

        return text;
    }
}
