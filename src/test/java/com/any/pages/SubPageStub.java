package com.any.pages;

import com.mgiorda.testng.AbstractPage;

public class SubPageStub extends AbstractPage {

	private static final String RELATIVE_URL = "/user";

	public SubPageStub(SampleGooglePage stubPage) {
		super(stubPage, RELATIVE_URL);
	}

}
