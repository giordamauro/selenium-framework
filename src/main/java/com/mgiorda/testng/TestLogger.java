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
		long threadId = Thread.currentThread().getId();
		logger.info(String.format("[thread: %s] - Starting test method '%s.%s(..)'", threadId, result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		long threadId = Thread.currentThread().getId();
		logger.info(String.format("[thread: %s] - Finished test method '%s.%s(..)' - PASSED", threadId, result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestFailure(ITestResult result) {
		long threadId = Thread.currentThread().getId();
		logger.warn(String.format("[thread: %s] - Finished test method '%s.%s(..)' - FAILED", threadId, result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		long threadId = Thread.currentThread().getId();
		logger.warn(String.format("[thread: %s] - Skipped test method '%s.%s(..)' - SKIPPED", threadId, result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		long threadId = Thread.currentThread().getId();
		logger.warn(String.format("[thread: %s] - Finished test method '%s.%s(..)' - FAILED", threadId, result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onStart(ITestContext context) {
		long threadId = Thread.currentThread().getId();
		logger.info(String.format("[thread: %s] - Initiating test named: '%s'", threadId, context.getName()));

	}

	@Override
	public void onFinish(ITestContext context) {
		long threadId = Thread.currentThread().getId();
		logger.info(String.format("[thread: %s] - Finished test named: '%s'", threadId, context.getName()));
	}
}
