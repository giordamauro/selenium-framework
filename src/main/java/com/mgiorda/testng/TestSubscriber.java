package com.mgiorda.testng;

import org.testng.ITestResult;

public interface TestSubscriber {

    void onClassStart(AbstractTest test);

    void onTestStart(ITestResult testResult);

    void onTestFinish(ITestResult testResult);

    void onClassFinish(AbstractTest test);
}
