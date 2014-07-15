package com.mgiorda.page.impl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;

import com.mgiorda.page.AbstractPage;
import com.mgiorda.page.DriverActionHandler;
import com.mgiorda.page.PageElementHandler;
import com.mgiorda.page.PageHandlerFactory;

public class PageHandlerFactoryImpl implements PageHandlerFactory {

    private final WebDriver driver;
    private final WebDriverWait driverWait;
    private final long timeOutInSeconds;

    public PageHandlerFactoryImpl(WebDriver driver, long timeOutInSeconds) {
        this.driver = driver;
        this.timeOutInSeconds = timeOutInSeconds;

        if (timeOutInSeconds != 0L) {
            this.driverWait = new WebDriverWait(driver, timeOutInSeconds);
        } else {
            this.driverWait = null;
        }
    }

    @Override
    public PageElementHandler getElementHandler(AbstractPage page) {

        ApplicationContext applicationContext = page.getApplicationContext();

        DriverElementHandler driverElementHandler = new DriverElementHandler(driver, driverWait, applicationContext);
        PageElementHandler pageElementHandler = new PageElementHandlerImpl(driverElementHandler, page);

        return pageElementHandler;
    }

    @Override
    public DriverActionHandler getActionHandler() {

        DriverActionHandler actionHandler = new DriverActionHandlerImpl(driver, driverWait, timeOutInSeconds);

        return actionHandler;
    }

}
