package com.any.tests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.any.pages.SampleGooglePage;

@ContextConfiguration(locations = { "classpath:/testsContext.xml" })
public class StubTest extends AbstractTestNGSpringContextTests {

	@Value("test.host")
	private String host;

	@Test
	public void test() {

		SampleGooglePage page = new SampleGooglePage();

		page.search(host);

		page.quit();
	}

}
