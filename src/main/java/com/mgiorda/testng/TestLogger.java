package com.mgiorda.testng;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestLogger implements ITestListener {

	private static final Log logger = LogFactory.getLog(TestLogger.class);

	@Override
	public void onTestStart(ITestResult result) {

		logger.info(String.format("Starting test method '%s.%s(..)'", result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		logger.info(String.format("Finished test method '%s.%s(..)' - PASSED", result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestFailure(ITestResult result) {

		logger.warn(String.format("Finished test method '%s.%s(..)' - FAILED", result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		logger.warn(String.format("Skipped test method '%s.%s(..)' - SKIPPED", result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

		logger.warn(String.format("Finished test method '%s.%s(..)' - FAILED", result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onStart(ITestContext context) {

		logger.info(String.format("Initiating test named: '%s'", context.getName()));

	}

	@Override
	public void onFinish(ITestContext context) {

		logger.info(String.format("Finished test named: '%s'", context.getName()));
	}
}
