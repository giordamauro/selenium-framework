package com.mgiorda.page;

import org.openqa.selenium.TimeoutException;

public class ElementTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 4703626638337761022L;

    public ElementTimeoutException(String message, TimeoutException e) {
        super(message, e);
    }
}
