package com.mgiorda.testng;

import java.util.HashMap;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ITestResult;

final class TestPoolManager {

	private static final Map<Thread, ITestResult> threadTests = new HashMap<>();

	private TestPoolManager() {

	}

	public static void registerTest(ITestResult test) {

		Object testInstance = test.getInstance();
		if (!testInstance.getClass().isAssignableFrom(AbstractTest.class)) {

			throw new IllegalStateException("Cannot register non AbstractTest class - " + testInstance.getClass());
		}

		Thread thread = Thread.currentThread();
		threadTests.put(thread, test);
	}

	public static AbstractTest getCurrentTest() {

		ITestResult test = getCurrentTestResult();

		AbstractTest absTest = (AbstractTest) test.getInstance();
		return absTest;
	}

	public static ISuite getCurrentTestSuite() {

		ITestResult currentTest = getCurrentTestResult();
		ISuite suite = currentTest.getTestContext().getSuite();

		return suite;
	}

	private static ITestResult getCurrentTestResult() {
		Thread thread = Thread.currentThread();

		ITestResult test = threadTests.get(thread);
		if (test == null) {
			throw new IllegalStateException(String.format("Exception reading ITestResult for thread id %s", thread.getId()));
		}

		return test;
	}
}
