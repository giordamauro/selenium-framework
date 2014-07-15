package com.mgiorda.page.elements;

import com.mgiorda.page.AbstractElement;

public class TextField extends AbstractElement {

    public void setText(CharSequence... keysToSend) {

        String keys = "";
        for (CharSequence seq : keysToSend) {
            keys += seq.toString();
        }

        logger.info(String.format("Setting text: '%s' in element", keys) + getNameInfo());

        pageElement.clear();
        pageElement.sendKeys(keysToSend);
    }

    public void submit() {

        logger.info("Submitting on element" + getNameInfo());

        pageElement.submit();
    }
}
