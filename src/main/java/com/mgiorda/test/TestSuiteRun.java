package com.mgiorda.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

public class TestSuiteRun {

	private final String suiteXml;

	private String outputDirectory = null;

	// private String[] locations;

	private Properties properties;

	public TestSuiteRun(String suiteXml) {
		this.suiteXml = suiteXml;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getSuiteXml() {
		return suiteXml;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	void runSuite() {

		TestNG testng = new TestNG();
		Collection<XmlSuite> suites;
		try {
			suites = new Parser(suiteXml).parse();
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Exception reading suite file '%s'", suiteXml));
		}
		testng.setXmlSuites(new ArrayList<XmlSuite>(suites));

		if (properties != null) {
			TestThreadPoolManager.registerSuiteProperties(properties);
		}

		if (outputDirectory != null) {
			testng.setOutputDirectory(outputDirectory);
		}

		testng.run();
	}

}
