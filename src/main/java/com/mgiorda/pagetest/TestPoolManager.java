package com.mgiorda.pagetest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ITestResult;

final class TestPoolManager {

	private static final Map<Thread, ITestResult> threadTests = new HashMap<>();
	private static final Map<ITestResult, List<AbstractPage>> testPages = new HashMap<>();

	private TestPoolManager() {

	}

	public synchronized static void registerTest(ITestResult test) {

		Object testInstance = test.getInstance();
		if (!AbstractTest.class.isAssignableFrom(testInstance.getClass())) {

			throw new IllegalStateException("Cannot register non AbstractTest class - " + testInstance.getClass());
		}

		Thread thread = Thread.currentThread();
		threadTests.put(thread, test);
	}

	public synchronized static void registerPage(AbstractPage page) {

		ITestResult test = getCurrentTestResult();

		List<AbstractPage> pages = testPages.get(test);
		if (pages == null) {
			pages = new ArrayList<>();
			testPages.put(test, pages);
		}

		pages.add(page);
	}

	public static void finishPages(ITestResult test) {

		List<AbstractPage> pages = testPages.get(test);
		if (pages != null) {
			for (AbstractPage page : pages) {
				page.onTestFinish();
			}
		}
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
