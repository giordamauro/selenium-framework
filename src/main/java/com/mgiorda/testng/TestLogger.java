package com.mgiorda.testng;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestLogger implements ITestListener, ISuiteListener {

	private static final Log logger = LogFactory.getLog(TestLogger.class);

	@Override
	public void onStart(ISuite suite) {

		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(String.format("Starting test suite '%s'", suite.getName()));
	}

	@Override
	public void onStart(ITestContext context) {

		logger.info(String.format(">> Initiating test named: '%s'", context.getName()));
	}

	@Override
	public void onTestStart(ITestResult result) {

		logger.info(String.format(">-------------------- Starting test method '%s.%s(..)'", result.getTestClass().getName(), result.getMethod().getMethodName()));
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		logger.info(String.format("<-------------------- Finished test method '%s.%s(..)' - PASSED after %s milliseconds", result.getTestClass().getName(), result.getMethod().getMethodName(),
				getTotalTime(result)));
	}

	@Override
	public void onTestFailure(ITestResult result) {

		logger.warn(String.format("<-------------------- Finished test method '%s.%s(..)' - FAILED after %s milliseconds", result.getTestClass().getName(), result.getMethod().getMethodName(),
				getTotalTime(result)), result.getThrowable());
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		logger.warn(String.format("<-------------------- Skipped test method '%s.%s(..)' - SKIPPED after %s milliseconds", result.getTestClass().getName(), result.getMethod().getMethodName(),
				getTotalTime(result)), result.getThrowable());
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

		logger.warn(String.format("<-------------------- Finished test method '%s.%s(..)' - FAILED", result.getTestClass().getName(), result.getMethod().getMethodName()), result.getThrowable());
	}

	@Override
	public void onFinish(ITestContext context) {

		logger.info(String.format("<< Finished test named: '%s'", context.getName()));
	}

	@Override
	public void onFinish(ISuite suite) {

		logger.info(String.format("Finished test suite '%s'", suite.getName()));

		String outputDirectory = suite.getOutputDirectory();
		logger.info(String.format("Logging '%s' suite test results to directory '%s'", suite.getName(), outputDirectory));
		logger.info(String.format("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"));
	}

	private long getTotalTime(ITestResult result) {
		long time = result.getEndMillis() - result.getStartMillis();
		return time;
	}
}
