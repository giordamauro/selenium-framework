package com.mgiorda.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class SuiteLogger implements ISuiteListener {

	private static final Log logger = LogFactory.getLog(SuiteLogger.class);

	@Override
	public void onStart(ISuite suite) {
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(String.format("Starting test suite '%s'", suite.getName()));

		TestThreadPoolManager.registerSuite(suite);
	}

	@Override
	public void onFinish(ISuite suite) {

		logger.info(String.format("Finished test suite '%s'", suite.getName()));

		String outputDirectory = suite.getOutputDirectory();
		logger.info(String.format("Logging '%s' suite test results to directory '%s'", suite.getName(), outputDirectory));
		logger.info(String.format("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"));
	}
}
