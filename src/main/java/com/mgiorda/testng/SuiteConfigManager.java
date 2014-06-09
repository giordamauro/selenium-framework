package com.mgiorda.testng;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.ISuite;

public final class SuiteConfigManager {

	private static final Log logger = LogFactory.getLog(SuiteConfigManager.class);

	private static final Map<Thread, ISuite> threadSuites = new HashMap<>();
	private static final Map<ISuite, TestConfiguration> suiteConfigs = new HashMap<>();

	private static TestConfiguration defaultTestConfig;

	private SuiteConfigManager() {

	}

	public static void setDefaultConfiguration(TestConfiguration testConfig) {
		defaultTestConfig = testConfig;
	}

	public void setTestConfiguration(ISuite suite, TestConfiguration suiteConfig) {
		suiteConfigs.put(suite, suiteConfig);
	}

	static void registerTestThread(ISuite suite) {

		Thread thread = Thread.currentThread();
		threadSuites.put(thread, suite);
	}

	public static TestConfiguration getTestConfiguration() {

		Thread thread = Thread.currentThread();

		ISuite suite = threadSuites.get(thread);
		if (suite == null) {
			throw new IllegalStateException(String.format("Exception reading Suite for thread id %s", thread.getId()));
		}

		TestConfiguration testConfig = getTestConfigurationForSuite(suite);

		return testConfig;
	}

	static TestConfiguration getTestConfigurationForSuite(ISuite suite) {

		TestConfiguration testConfig = suiteConfigs.get(suite);
		if (testConfig == null) {

			if (defaultTestConfig == null) {
				throw new IllegalStateException("Default test configuration is not set");
			}

			logger.info(String.format("Setting default test configuration for suite %s", suite.getName()));
			suiteConfigs.put(suite, defaultTestConfig);

			testConfig = defaultTestConfig;
		}

		return testConfig;
	}
}
