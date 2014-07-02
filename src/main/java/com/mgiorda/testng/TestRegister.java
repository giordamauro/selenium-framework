package com.mgiorda.testng;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestRegister implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {
		CurrentTestRun.registerTestResult(result);
	}

	@Override
	public void onTestSuccess(ITestResult result) {

	}

	@Override
	public void onTestFailure(ITestResult result) {
	}

	@Override
	public void onTestSkipped(ITestResult result) {
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

	@Override
	public void onStart(ITestContext context) {
		CurrentTestRun.registerTestContext(context);
	}

	@Override
	public void onFinish(ITestContext context) {
	}

}
