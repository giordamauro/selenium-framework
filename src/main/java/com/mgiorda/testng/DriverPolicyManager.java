package com.mgiorda.testng;

import java.util.HashMap;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ITestResult;

public final class DriverPolicyManager {

	// cambiar TestNGMethod por algo diferente en runtime

	private static final Map<Thread, ITestResult> threadTests = new HashMap<>();

	private static final Map<ITestResult, DriverPoolManager> testDrivers = new HashMap<>();
	private static final Map<Class<?>, DriverPoolManager> classDrivers = new HashMap<>();
	private static final Map<ISuite, DriverPoolManager> suiteDrivers = new HashMap<>();

	private DriverPolicyManager() {

	}

	public static void registerTestThread(ITestResult test) {
		Thread thread = Thread.currentThread();
		threadTests.put(thread, test);
	}

	static DriverPoolManager getDriverPoolManager(TestConfiguration testConfig) {

		DriverPoolManager driverManager = null;

		DriverPolicy driverPolicy = testConfig.getDriverPolicy();

		ITestResult test = getTestForCurrentThread();
		if (driverPolicy == DriverPolicy.FINISH_PER_TEST) {

			driverManager = testDrivers.get(test);

			if (driverManager == null) {
				driverManager = new DriverPoolManager(testConfig.getDriverHandler(), testConfig.getBrowser());
				testDrivers.put(test, driverManager);
			}
		} else if (driverPolicy == DriverPolicy.FINISH_PER_CLASS) {

			Class<?> testClass = test.getTestClass().getRealClass();
			driverManager = classDrivers.get(testClass);

			if (driverManager == null) {
				driverManager = new DriverPoolManager(testConfig.getDriverHandler(), testConfig.getBrowser());
				classDrivers.put(testClass, driverManager);
			}
		} else if (driverPolicy == DriverPolicy.FINISH_PER_SUITE) {

			ISuite suite = test.getTestContext().getSuite();

			driverManager = suiteDrivers.get(suite);

			if (driverManager == null) {
				driverManager = new DriverPoolManager(testConfig.getDriverHandler(), testConfig.getBrowser());
				suiteDrivers.put(suite, driverManager);
			}
		} else {
			throw new IllegalStateException(String.format("Unsupported operation for driverPolicy %s - Not done yet", driverPolicy));
		}

		return driverManager;
	}

	public static void quitTestDrivers(ITestResult test) {
		DriverPoolManager driverManager = testDrivers.get(test);
		if (driverManager != null) {
			driverManager.quitAllDrivers();
			testDrivers.remove(test);
		}
	}

	public static void quitClassDrivers(Class<?> testClass) {
		DriverPoolManager driverManager = classDrivers.get(testClass);
		if (driverManager != null) {
			driverManager.quitAllDrivers();
			classDrivers.remove(testClass);
		}
	}

	public static void quitSuiteDrivers(ISuite suite) {
		DriverPoolManager driverManager = suiteDrivers.get(suite);
		if (driverManager != null) {
			driverManager.quitAllDrivers();
			suiteDrivers.remove(suite);
		}
	}

	private static ITestResult getTestForCurrentThread() {

		Thread thread = Thread.currentThread();

		ITestResult test = threadTests.get(thread);
		if (test == null) {
			throw new IllegalStateException(String.format("Exception reading ITestResult for thread id %s", thread.getId()));
		}

		return test;
	}

}
