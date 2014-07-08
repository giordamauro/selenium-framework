package com.mgiorda.testng.run;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultipleSuiteRunner<E extends SuiteConfiguration> {

	private static final Log logger = LogFactory.getLog(MultipleSuiteRunner.class);

	private static final UncaughtExceptionHandler exceptionLogger = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable e) {
			logger.warn(String.format("Exception in thread name: '%s' id '%s'", thread.getName(), thread.getId()), e);
		}
	};

	private final SuiteRunner<E> suiteRunner;

	public MultipleSuiteRunner(SuiteRunner<E> suiteRunner) {
		this.suiteRunner = suiteRunner;
	}

	public void run(List<E> suites, boolean runInParallel) {

		for (final E suite : suites) {

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					suiteRunner.runSuite(suite);
				}
			};

			if (runInParallel) {
				Thread thread = new Thread(runnable);
				thread.setUncaughtExceptionHandler(exceptionLogger);

				logger.info(String.format("Starting TestNG run in thread %s for suite file '%s'", thread.getId(), suite.getFile()));

				thread.start();
			} else {
				logger.info(String.format("Starting TestNG sequential run for suite file '%s'", suite.getFile()));

				runnable.run();
			}
		}
	}
}
