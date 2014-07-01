package com.mgiorda.testng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestEventDispatcher implements ITestListener {

	private static final Map<AbstractTest, TestEventDispatcher> eventDispatchers = new HashMap<>();

	private List<TestSubscriber> instanceSubscribers = new ArrayList<>();
	private List<TestSubscriber> resultSubscribers = new ArrayList<>();

	public synchronized void subscribe(TestSubscriber subscriber) {

		ITestResult testResult = CurrentTestRun.getTestResult();
		if (testResult != null) {
			resultSubscribers.add(subscriber);
		} else {
			instanceSubscribers.add(subscriber);
		}
	}

	public static TestEventDispatcher getEventDispatcher() {

		AbstractTest testInstance = CurrentTestRun.getTestInstance();
		if (testInstance == null) {
			throw new IllegalStateException("Cannot get TestEventDispatcher - Not running under any AbstractTest instance");
		}

		TestEventDispatcher testEventDispatcher = eventDispatchers.get(testInstance);
		if (testEventDispatcher == null) {

			testEventDispatcher = new TestEventDispatcher();
			eventDispatchers.put(testInstance, testEventDispatcher);
		}

		return testEventDispatcher;
	}

	@Override
	public void onStart(ITestContext context) {
	}

	@Override
	public void onTestStart(ITestResult result) {

		for (TestSubscriber subscriber : resultSubscribers) {
			subscriber.onTestStart(result);
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		onTestFinish(result);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		onTestFinish(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		onTestFinish(result);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		onTestFinish(result);
	}

	@Override
	public void onFinish(ITestContext context) {

	}

	void onClassStart(AbstractTest test) {

		for (TestSubscriber subscriber : instanceSubscribers) {
			subscriber.onClassStart(test);
		}
	}

	void onClassFinish(AbstractTest test) {

		for (TestSubscriber subscriber : instanceSubscribers) {
			subscriber.onClassFinish(test);
		}

		eventDispatchers.remove(test);
	}

	private void onTestFinish(ITestResult result) {

		TestEventDispatcher eventDispatcher = getEventDispatcher();

		for (TestSubscriber subscriber : eventDispatcher.resultSubscribers) {
			subscriber.onTestFinish(result);
		}
	}
}
