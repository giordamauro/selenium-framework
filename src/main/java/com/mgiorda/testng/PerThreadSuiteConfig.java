package com.mgiorda.testng;

public final class PerThreadSuiteConfig {

	private static final ThreadLocal<SuiteConfiguration> suiteConfigHolder = new ThreadLocal<SuiteConfiguration>();

	private PerThreadSuiteConfig() {

	}

	public static void setConfiguration(SuiteConfiguration suiteConfig) {
		suiteConfigHolder.set(suiteConfig);
	}

	public static SuiteConfiguration getConfiguration() {

		SuiteConfiguration value = suiteConfigHolder.get();
		if (value == null) {
			throw new IllegalStateException("Suite configuration values are not set");
		}

		return value;
	}

	public static boolean isConfigurationSet() {
		SuiteConfiguration value = suiteConfigHolder.get();

		return value != null;
	}
}
