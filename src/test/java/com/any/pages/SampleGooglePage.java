package com.any.pages;

import org.openqa.selenium.support.FindBy;

import com.mgiorda.testng.AbstractPage;

public class SampleGooglePage extends AbstractPage {

	@FindBy(name = "q")
	private PageElement searchBox;

	public SampleGooglePage() {
		super("http://www.google.com");
	}

	public void search(String text) {
		logger.info("logging something....");
		searchBox.sendKeys(text);
	}

	public SubPageStub goToSubPage() {
		return new SubPageStub(this);
	}
}
