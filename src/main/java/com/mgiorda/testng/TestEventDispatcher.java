package com.mgiorda.testng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ITestResult;

public class TestEventDispatcher {

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
        }
        instanceSubscribers.add(subscriber);
    }

    void onClassStart(AbstractTest test) {

        for (TestSubscriber subscriber : instanceSubscribers) {
            subscriber.onClassStart(test);
        }
    }

    void onTestStart(ITestResult result) {

        List<TestSubscriber> subscribers = resultSubscribers.get(result);

        if (subscribers != null) {
            for (TestSubscriber subscriber : subscribers) {
                subscriber.onTestStart(result);
            }
        }

        for (TestSubscriber subscriber : instanceSubscribers) {
            subscriber.onTestStart(result);
        }
    }

    void onTestFinish(ITestResult result) {

        List<TestSubscriber> subscribers = resultSubscribers.get(result);

        if (subscribers != null) {
            for (TestSubscriber subscriber : subscribers) {
                subscriber.onTestFinish(result);
            }
        }

        resultSubscribers.remove(result);

        for (TestSubscriber subscriber : instanceSubscribers) {
            subscriber.onTestFinish(result);
        }
    }

    void onClassFinish(AbstractTest test) {

        for (TestSubscriber subscriber : instanceSubscribers) {
            subscriber.onClassFinish(test);
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
}
