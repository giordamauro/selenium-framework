package com.any.tests;

import org.testng.annotations.Test;

import com.any.pages.SampleGooglePage;

public class StubTest {

	@Test
	public void test() {

		SampleGooglePage page = new SampleGooglePage();

		page.search("Hola que tal");

		page.quit();
	}

}
