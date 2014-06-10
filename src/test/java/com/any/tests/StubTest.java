package com.any.tests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.testng.annotations.Test;

import com.any.pages.SampleGooglePage;
import com.mgiorda.testng.AbstractTest;

@PropertySource("classpath:/my.properties")
public class StubTest extends AbstractTest {

	@Value("${test.host}")
	private String host;

	@Test(invocationCount = 3, threadPoolSize = 2)
	public void test() {

		SampleGooglePage page = new SampleGooglePage();

		page.search(host);
	}

}
