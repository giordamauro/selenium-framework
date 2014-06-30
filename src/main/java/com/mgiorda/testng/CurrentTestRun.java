package com.mgiorda.testng;

import java.util.HashMap;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

public final class CurrentTestRun {

	private static final Map<Thread, ITestResult> testResults = new HashMap<>();
	private static final Map<Thread, AbstractTest> testInstances = new HashMap<>();
	private static final Map<Thread, ITestContext> testContexts = new HashMap<>();

	private CurrentTestRun() {
	}

	public static ISuite getSuite() {

		ISuite suite = null;

		ITestContext context = getTestContext();
		if (context != null) {
			suite = context.getSuite();
		}

		return suite;
	}

	public static ITestContext getTestContext() {

		ITestContext context = null;

		ITestResult testResult = getTestResult();
		if (testResult != null) {
			context = testResult.getTestContext();
		} else {
			Thread thread = Thread.currentThread();
			context = testContexts.get(thread);
		}

		return context;
	}

	public static <T extends AbstractTest> T getTestInstance() {

		Object testInstance = null;

		ITestResult testResult = getTestResult();
		if (testResult != null) {
			testInstance = testResult.getInstance();
		} else {
			Thread thread = Thread.currentThread();
			testInstance = testInstances.get(thread);
		}

		@SuppressWarnings("unchecked")
		T castedInstance = (T) testInstance;

		return castedInstance;
	}

	public static ITestResult getTestResult() {

		Thread thread = Thread.currentThread();
		ITestResult testResult = testResults.get(thread);

		return testResult;
	}

	static synchronized void registerTestResult(ITestResult testResult) {

		Thread thread = Thread.currentThread();
		testResults.put(thread, testResult);
	}

	static synchronized void registerTestInstance(AbstractTest testInstance) {

		Thread thread = Thread.currentThread();
		testInstances.put(thread, testInstance);
	}

	static synchronized void registerTestContext(ITestContext context) {

		Thread thread = Thread.currentThread();
		testContexts.put(thread, context);
	}

	static void unRegisterTestResult(ITestResult testResult) {

		Thread thread = Thread.currentThread();
		ITestResult savedTestResult = testResults.get(thread);

		if (savedTestResult == null || !savedTestResult.equals(testResult)) {
			throw new IllegalStateException(String.format("Exception unregistering testResult '%s' for thread '%s'", testResult, thread));
		}

		testResults.remove(thread);
	}

	static void unRegisterTestInstance(AbstractTest abstractTest) {

		Thread thread = Thread.currentThread();
		AbstractTest savedTestInstance = testInstances.get(thread);

		if (savedTestInstance == null || !savedTestInstance.equals(abstractTest)) {
			throw new IllegalStateException(String.format("Exception unregistering testInstance '%s' for thread '%s'", abstractTest, thread));
		}

		testInstances.remove(thread);
	}

	static void unRegisterTestContext(ITestContext context) {

		Thread thread = Thread.currentThread();
		ITestContext savedTestContext = testContexts.get(thread);

		if (savedTestContext == null || !savedTestContext.equals(context)) {
			throw new IllegalStateException(String.format("Exception unregistering testContext '%s' for thread '%s'", context, thread));
		}

		testContexts.remove(thread);
	}
}
