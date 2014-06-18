package com.mgiorda.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

import com.mgiorda.commons.SpringUtil;

public class TestPoolRunner {

	private static final Log logger = LogFactory.getLog(TestPoolRunner.class);

	private static final UncaughtExceptionHandler exceptionLogger = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable e) {
			logger.warn(String.format("Exception in thread name: '%s' id '%s'", thread.getName(), thread.getId()), e);
		}
	};

	private final SuiteTestRun[] suites;

	public TestPoolRunner(SuiteTestRun... suites) {
		this.suites = suites;
	}

	public void run() {

		for (SuiteTestRun suite : suites) {

			Runnable runnable = getRunnableForSuite(suite);

			Thread thread = new Thread(runnable);
			thread.setUncaughtExceptionHandler(exceptionLogger);

			logger.info(String.format("Starting TestNG run thread %s - Suite '%s'", thread.getId(), suite.getFile()));

			thread.start();
		}
	}

	private Runnable getRunnableForSuite(final SuiteTestRun suite) {

		return new Runnable() {

			@Override
			public void run() {

				String suiteXml = suite.getFile();
				if (suiteXml == null) {
					throw new IllegalStateException("Property 'file' in SuiteTestRun cannot be null - Suite " + suite.hashCode());
				}

				TestNG testng = new TestNG();
				Collection<XmlSuite> suites;
				try {
					File suiteFile = SpringUtil.getClasspathFile(suiteXml);

					InputStream suiteFileInputStream = new FileInputStream(suiteFile);
					suites = new Parser(suiteFileInputStream).parse();
				} catch (Exception e) {
					throw new IllegalStateException(String.format("Exception reading suite file '%s'", suiteXml));
				}
				testng.setXmlSuites(new ArrayList<XmlSuite>(suites));

				TestThreadPoolManager.registerSuiteProperties(suite.getProperties());

				String outputDirectory = suite.getOutputDirectory();
				if (outputDirectory != null) {
					testng.setOutputDirectory(outputDirectory);
				}

				testng.run();
			}
		};
	}
}
