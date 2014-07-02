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
	private Map<ITestResult, List<TestSubscriber>> resultSubscribers = new HashMap<>();

	public synchronized void subscribe(TestSubscriber subscriber) {

		ITestResult testResult = CurrentTestRun.getTestResult();
		if (testResult != null) {

			List<TestSubscriber> subscribers = resultSubscribers.get(testResult);
			if (subscribers == null) {
				subscribers = new ArrayList<>();
				resultSubscribers.put(testResult, subscribers);
			}
			subscribers.add(subscriber);
		} else {
			instanceSubscribers.add(subscriber);
		}
	}

	public static TestEventDispatcher getEventDispatcher() {

		AbstractTest testInstance = CurrentTestRun.getTestInstance();
		if (testInstance == null) {
			throw new IllegalStateException("Cannot get TestEventDispatcher - Not running under any AbstractTest instance");
		}

		TestEventDispatcher testEventDispatcher = getEventDispatcher(testInstance);

		return testEventDispatcher;
	}

	public static TestEventDispatcher getEventDispatcher(AbstractTest testInstance) {

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

		TestEventDispatcher eventDispatcher = getEventDispatcher();

		List<TestSubscriber> subscribers = eventDispatcher.resultSubscribers.get(result);

		if (subscribers != null) {
			for (TestSubscriber subscriber : subscribers) {
				subscriber.onTestStart(result);
			}
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

		AbstractTest testInstance = (AbstractTest) result.getInstance();
		TestEventDispatcher eventDispatcher = eventDispatchers.get(testInstance);

		List<TestSubscriber> subscribers = eventDispatcher.resultSubscribers.get(result);

		if (subscribers != null) {
			for (TestSubscriber subscriber : subscribers) {
				subscriber.onTestFinish(result);
			}
		}

		eventDispatcher.resultSubscribers.remove(result);
	}
}
