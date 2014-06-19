package com.mgiorda.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.mgiorda.test.TestRunner;

public final class MainRunner {

	public static final String DEFAULT_SUITE_CONTEXT = "suite-context.xml";

	private MainRunner() {

	}

	public static void runDefault() {

		runForContext(DEFAULT_SUITE_CONTEXT);
	}

	public static void runForArgs(String[] args) {

		String suiteContext = DEFAULT_SUITE_CONTEXT;

		if (args != null && args.length == 1) {
			suiteContext = args[0];
		}

		runForContext(suiteContext);
	}

	public static void runForContext(String suiteContext) {

		@SuppressWarnings("resource")
		ApplicationContext applicationContext = new GenericXmlApplicationContext(suiteContext);

		TestRunner testRunner = applicationContext.getBean(TestRunner.class);

		testRunner.run();
	}
}
