package com.mgiorda.testng;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import com.any.tests.StubTest;

public class TestSuiteRunner implements Runnable {

	private final TestConfiguration testConfig;

	public TestSuiteRunner(TestConfiguration testConfig) {

		this.testConfig = testConfig;

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {

		PerThreadTestConfig.setConfiguration(testConfig);

		TestNG testng = new TestNG();

		// XmlSuite suite = new XmlSuite();
		// suite.setName("TmpSuite");

		// XmlTest test = new XmlTest(suite);
		// test.setName("TmpTest");
		// List<XmlClass> classes = new ArrayList<XmlClass>();
		// classes.add(new XmlClass("test.failures.Child"));
		// test.setXmlClasses(classes);

		// testng.setXmlSuites(Collections.singletonList(suite));

		testng.setTestClasses(new Class[] { StubTest.class });

		TestListenerAdapter listenerAdapter = new TestListenerAdapter();
		testng.addListener(listenerAdapter);

		testng.run();
	}
}
