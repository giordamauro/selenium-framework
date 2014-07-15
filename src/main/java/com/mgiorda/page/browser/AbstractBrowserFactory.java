package com.mgiorda.page.browser;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.FactoryBean;

public abstract class AbstractBrowserFactory implements BrowserFactory, FactoryBean<WebDriver> {

    @Override
    public WebDriver getObject() throws Exception {
        return newDriver();
    }

    @Override
    public Class<?> getObjectType() {
        return WebDriver.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
