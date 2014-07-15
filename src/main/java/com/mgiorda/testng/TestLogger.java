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

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.info(String.format(">>>> Starting suite '%s'", suite.getName()));
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

        String outputDirectory = suite.getOutputDirectory();
        logger.debug(String.format("Saving '%s' results in directory: '%s'", suite.getName(), outputDirectory));

        logger.info(String.format("<<<< Finishing suite '%s'", suite.getName()));
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    private long getTotalTime(ITestResult result) {
        long time = result.getEndMillis() - result.getStartMillis();
        return time;
    }
}
