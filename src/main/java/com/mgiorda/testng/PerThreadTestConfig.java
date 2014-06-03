package com.mgiorda.testng;

public final class PerThreadTestConfig {

	private static final ThreadLocal<TestConfiguration> testConfigHolder = new ThreadLocal<TestConfiguration>();

	private PerThreadTestConfig() {

	}

	public static void setConfiguration(TestConfiguration testConfig) {
		testConfigHolder.set(testConfig);
	}

	public static TestConfiguration getConfiguration() {

		TestConfiguration value = testConfigHolder.get();
		if (value == null) {
			throw new IllegalStateException("Test thread configuration not set");
		}

		return value;
	}

	public static boolean isConfigurationSet() {
		TestConfiguration value = testConfigHolder.get();

		return value != null;
	}

}
