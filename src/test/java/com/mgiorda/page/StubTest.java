package com.mgiorda.page;

import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.any.pages.SampleGooglePage;
import com.mgiorda.context.AbstractTest;
import com.mgiorda.context.Context;
import com.mgiorda.context.Properties;

@Properties("test-properties/${suite.env}-test.properties")
@Context
public class StubTest extends AbstractTest {

    @Value("${${suite.env}.host}")
    private String host;

    @Test(invocationCount = 3, threadPoolSize = 2)
    public void test() {

        SampleGooglePage page = new SampleGooglePage();

        logger.info("Created Google page");

        // page.clickOnDoodle();
        page.search(host);

        Assert.assertEquals(page.getTitle(), "Google");
    }
}
