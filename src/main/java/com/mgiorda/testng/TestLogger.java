package com.mgiorda.testng;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.mgiorda.selenium.DriverPolicyManager;

public class TestLogger implements ITestListener {

	private static final Log logger = LogFactory.getLog(TestLogger.class);

	@Override
	public void onTestStart(ITestResult result) {

		logger.info(String.format("Starting test method '%s.%s(..)'", result.getTestClass().getName(), result.getMethod().getMethodName()));

		ISuite suite = result.getTestContext().getSuite();
		SuiteConfigManager.registerTestThread(suite);
		DriverPolicyManager.registerTestThread(result);
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		logger.info(String.format("Finished test method '%s.%s(..)' - PASSED after %s milliseconds", result.getTestClass().getName(), result.getMethod().getMethodName(), getTotalTime(result)));

		DriverPolicyManager.quitTestDrivers(result);
	}

	@Override
	public void onTestFailure(ITestResult result) {

		logger.warn(String.format("Finished test method '%s.%s(..)' - FAILED after %s milliseconds", result.getTestClass().getName(), result.getMethod().getMethodName(), getTotalTime(result)));

		DriverPolicyManager.quitTestDrivers(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		logger.warn(String.format("Skipped test method '%s.%s(..)' - SKIPPED after %s milliseconds", result.getTestClass().getName(), result.getMethod().getMethodName(), getTotalTime(result)));

		DriverPolicyManager.quitTestDrivers(result);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

		logger.warn(String.format("Finished test method '%s.%s(..)' - FAILED", result.getTestClass().getName(), result.getMethod().getMethodName()));

		DriverPolicyManager.quitTestDrivers(result);
	}

	@Override
	public void onStart(ITestContext context) {

		logger.info(String.format("Initiating test named: '%s'", context.getName()));

	}

	@Override
	public void onFinish(ITestContext context) {

		logger.info(String.format("Finished test named: '%s'", context.getName()));
	}

	private long getTotalTime(ITestResult result) {
		long time = result.getEndMillis() - result.getStartMillis();
		return time;
	}
}
