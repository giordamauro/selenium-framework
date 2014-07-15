package com.any.pages;

import org.springframework.beans.factory.annotation.Value;

import com.mgiorda.context.Properties;
import com.mgiorda.page.AbstractPage;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;
import com.mgiorda.page.annotations.PageURL;
import com.mgiorda.page.elements.TextField;

@Properties("page-properties/page.properties")
@PageURL("${dev.host}/")
public class SampleGooglePage extends AbstractPage {

    @Locate(@By(name = "${locator.searchBox.name}"))
    private TextField searchBox;

    // @Locate(@By(id = "hplogo"))
    // private GoogleDoodle doodle;

    @Locate(@By(id = "fbar"))
    private FooterBar footer;

    @Value("${page.someProperty}")
    private String someProperty;

    public void search(String text) {

        logger.info("logging host: " + someProperty);
        searchBox.setText(text);
    }

    // public void clickOnDoodle() {
    // doodle.click();
    // }
}
