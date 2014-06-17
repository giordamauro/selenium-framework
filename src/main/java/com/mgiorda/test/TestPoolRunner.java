package com.mgiorda.test;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestPoolRunner {

	private static final Log logger = LogFactory.getLog(TestPoolRunner.class);

	private static final UncaughtExceptionHandler exceptionLogger = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable e) {
			logger.warn(String.format("Exception in thread name: '%s' id '%s'", thread.getName(), thread.getId()), e);
		}
	};

	public TestPoolRunner(TestSuiteRun... suites) {

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

		logger.info(String.format("Starting TestNG run thread %s - Suite '%s'", thread.getId(), suite.getSuiteXml()));

		thread.start();

	}
}
