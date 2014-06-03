package com.mgiorda.testng;

public final class PerThreadTestConfig {

	private static TestConfiguration defaultTestConfiguration;

	private static final ThreadLocal<TestConfiguration> testConfigHolder = new ThreadLocal<TestConfiguration>();

	private PerThreadTestConfig() {

	}

	public static void setDefaultConfiguration(TestConfiguration testConfig) {
		defaultTestConfiguration = testConfig;
	}

	public static void setConfiguration(TestConfiguration testConfig) {
		testConfigHolder.set(testConfig);
	}

	public static TestConfiguration getConfiguration() {

		TestConfiguration value = testConfigHolder.get();
		if (value == null) {
			if (defaultTestConfiguration == null) {
				throw new IllegalStateException("Default test configuration per thread is not set");
			} else {
				return defaultTestConfiguration;
			}
		}

		return value;
	}

}
