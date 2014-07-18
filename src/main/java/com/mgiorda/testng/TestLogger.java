package com.mgiorda.testng;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestLogger implements ITestListener, ISuiteListener {

	private static final Log logger = LogFactory.getLog(TestLogger.class);

	private long startTime = -1;

	@Override
	public void onStart(ISuite suite) {

		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(String.format(">>>> Starting suite '%s'", suite.getName()));

		startTime = new Date().getTime();
	}

	@Override
	public void onStart(ITestContext context) {

		logger.info(String.format(">>>- Starting test '%s'", context.getName()));
	}

	@Override
	public void onTestStart(ITestResult result) {

		logger.info(String.format(">--- Starting test Method '%s' in %s", result.getMethod().getMethodName(), result.getTestClass().getName()));
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		logger.info(String.format("<--- Finishing test Method '%s' in %s - PASSED after %s milliseconds", result.getMethod().getMethodName(), result.getTestClass().getName(), getTotalTime(result)));
	}

	@Override
	public void onTestFailure(ITestResult result) {

		logger.warn(String.format("<--- Finishing test Method '%s' in %s - FAILED after %s milliseconds", result.getMethod().getMethodName(), result.getTestClass().getName(), getTotalTime(result)),
				result.getThrowable());
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		logger.warn(String.format("<--- Finishing test Method '%s' in %s - FAILED after %s milliseconds", result.getMethod().getMethodName(), result.getTestClass().getName(), getTotalTime(result)),
				result.getThrowable());
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

		logger.warn(String.format("<--- Finishing test Method '%s' in %s - FAILED after %s milliseconds", result.getMethod().getMethodName(), result.getTestClass().getName(), getTotalTime(result)),
				result.getThrowable());
	}

	@Override
	public void onFinish(ITestContext context) {

		logger.info(String.format("<<<- Finishing test '%s'", context.getName()));
	}

	@Override
	public void onFinish(ISuite suite) {

		long endTime = new Date().getTime();
		long millis = endTime - startTime;

		String readableTime = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

		String outputDirectory = suite.getOutputDirectory();
		logger.debug(String.format("Saving '%s' results in directory: '%s'", suite.getName(), outputDirectory));

		logger.info(String.format("<<<< Finishing suite '%s' - Took %s", suite.getName(), readableTime));
		logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}

	private long getTotalTime(ITestResult result) {
		long time = result.getEndMillis() - result.getStartMillis();
		return time;
	}
}
