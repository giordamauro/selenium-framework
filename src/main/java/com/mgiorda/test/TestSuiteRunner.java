package com.mgiorda.test;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class TestSuiteRunner implements Runnable {

	public TestSuiteRunner() {

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {

		TestNG testng = new TestNG();

		// XmlSuite suite = new XmlSuite();
		// suite.setName("TmpSuite");

		// XmlTest test = new XmlTest(suite);
		// test.setName("TmpTest");
		// List<XmlClass> classes = new ArrayList<XmlClass>();
		// classes.add(new XmlClass("test.failures.Child"));
		// test.setXmlClasses(classes);

		// testng.setXmlSuites(Collections.singletonList(suite));

		// testng.setTestClasses(new Class[] { StubTest.class });

		TestListenerAdapter listenerAdapter = new TestListenerAdapter();
		testng.addListener(listenerAdapter);

		testng.run();
	}
}
