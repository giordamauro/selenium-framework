package com.mgiorda.test;

import java.util.ArrayList;
import java.util.Collection;

import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

public class TestSuiteRunner implements Runnable {

	private final SuiteConfiguration suiteConfiguration;

	public TestSuiteRunner(SuiteConfiguration suiteConfiguration) {
		this.suiteConfiguration = suiteConfiguration;
	}

	@Override
	public void run() {

		String suiteXml = suiteConfiguration.getSuiteXml();

		TestNG testng = new TestNG();
		Collection<XmlSuite> suite;
		try {
			suite = new Parser(suiteXml).parse();
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Exception reading suite file '%s'", suiteXml));
		}
		testng.setXmlSuites(new ArrayList<XmlSuite>(suite));

		String outputDirectory = suiteConfiguration.getOutputDirectory();
		if (outputDirectory != null) {
			testng.setOutputDirectory(outputDirectory);
		}

		testng.run();
	}
}
