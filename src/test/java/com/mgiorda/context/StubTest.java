package com.mgiorda.context;

import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.Test;

@Properties("properties/test.properties")
public class StubTest extends AbstractTest {

	@Value("${suite.env}")
	private String defaultEnv;

	@Value("${my.property}")
	private String myProperty;

	@Value("${my.defaultClass.property}")
	private String defaultClassProperty;

	@Test
	public void testSomething() {
		System.out.println("This is my test");

		System.out.println("This is default Env " + defaultEnv);
		System.out.println("This is myProperty " + myProperty);
		System.out.println("This is myDefaultClassProperty " + defaultClassProperty);
	}

}
