package com.mgiorda.test;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mgiorda.pagetest.TestSuiteRun;

public class TestNGPoolRunner {

	private static final Log logger = LogFactory.getLog(TestNGPoolRunner.class);

	private static final UncaughtExceptionHandler exceptionLogger = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable e) {
			logger.warn(String.format("Exception in thread name: '%s' id '%s'", thread.getName(), thread.getId()), e);
		}
	};

	public TestNGPoolRunner(TestSuiteRun... suites) {

		for (TestSuiteRun suite : suites) {
			newTestThread(suite);
		}
	}

	private void newTestThread(final TestSuiteRun suite) {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				suite.runSuite();
			}
		};

		Thread thread = new Thread(runnable);
		thread.setUncaughtExceptionHandler(exceptionLogger);

		SuiteConfiguration configuration = suite.getConfiguration();
		logger.info(String.format("Starting TestNG run thread %s - Suite '%s'", thread.getId(), configuration.getSuiteXml()));

		thread.start();

	}
}
