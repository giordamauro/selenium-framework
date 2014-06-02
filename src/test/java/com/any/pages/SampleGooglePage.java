package com.any.pages;

import org.openqa.selenium.support.FindBy;

import com.mgiorda.selenium.AbstractPage;

public class SampleGooglePage extends AbstractPage {

	private static final String RELATIVE_URL = "/";

	@FindBy(name = "q")
	private PageElement searchBox;

	public SampleGooglePage() {
		super(RELATIVE_URL);
	}

	public void search(String text) {
		searchBox.sendKeys(text);
	}

	public SubPageStub goToSubPage() {
		return new SubPageStub(this);
	}

}
