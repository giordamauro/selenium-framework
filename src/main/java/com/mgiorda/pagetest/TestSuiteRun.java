package com.mgiorda.pagetest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

import com.mgiorda.test.SuiteConfiguration;

public class TestSuiteRun {

	private final SuiteConfiguration suiteConfiguration;

	public TestSuiteRun(SuiteConfiguration suiteConfiguration) {
		this.suiteConfiguration = suiteConfiguration;
	}

	public SuiteConfiguration getConfiguration() {
		return suiteConfiguration;
	}

	public void runSuite() {

		String suiteXml = suiteConfiguration.getSuiteXml();

		TestNG testng = new TestNG();
		Collection<XmlSuite> suites;
		try {
			suites = new Parser(suiteXml).parse();
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Exception reading suite file '%s'", suiteXml));
		}
		testng.setXmlSuites(new ArrayList<XmlSuite>(suites));

		Properties properties = suiteConfiguration.getProperties();
		if (properties != null) {
			TestPoolManager.registerSuiteProperties(properties);
		}

		String outputDirectory = suiteConfiguration.getOutputDirectory();
		if (outputDirectory != null) {
			testng.setOutputDirectory(outputDirectory);
		}

		testng.run();
	}
}
