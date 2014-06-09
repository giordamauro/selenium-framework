package com.mgiorda.testng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ITestResult;

import com.mgiorda.selenium.AbstractPage;

public final class TestPoolManager {

	private static final Map<Thread, ITestResult> threadTests = new HashMap<>();
	private static final Map<ITestResult, List<AbstractPage>> testPages = new HashMap<>();

	private TestPoolManager() {

	}

	synchronized static void registerTestThread(ITestResult test) {

		Thread thread = Thread.currentThread();
		threadTests.put(thread, test);
	}

	public synchronized static void registerTestPage(AbstractPage testPage) {
		ITestResult currentTest = getCurrentTest();

		List<AbstractPage> list = testPages.get(currentTest);
		if (list == null) {
			list = new ArrayList<>();
			testPages.put(currentTest, list);
		}
		list.add(testPage);
	}

	public synchronized static ITestResult getCurrentTest() {

		Thread thread = Thread.currentThread();

		ITestResult test = threadTests.get(thread);
		if (test == null) {
			throw new IllegalStateException(String.format("Exception reading ITestResult for thread id %s", thread.getId()));
		}

		return test;
	}

	public static ISuite getCurrentTestSuite() {

		ITestResult currentTest = getCurrentTest();
		ISuite suite = currentTest.getTestContext().getSuite();

		return suite;
	}
}
