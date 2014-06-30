package com.mgiorda.testng;

import org.testng.ITestContext;
import org.testng.ITestResult;

public interface TestSubscriber {

	void onClassStart(ITestContext testContext);

	void onTestStart(ITestResult testResult);

	void onTestFinish(ITestResult testResult);

	void onClassFinish(ITestContext testContext);

}
