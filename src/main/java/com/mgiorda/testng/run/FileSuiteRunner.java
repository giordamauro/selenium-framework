package com.mgiorda.testng.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

import com.mgiorda.context.SpringUtil;

public class FileSuiteRunner<E extends SuiteConfiguration> implements SuiteRunner<E> {

	@Override
	public void runSuite(E suiteConfig) {

		if (suiteConfig == null) {
			throw new IllegalStateException("Suite config cannot be null");
		}

		String suiteFile = suiteConfig.getFile();
		XmlSuite xmlSuite = getXmlSuite(suiteFile);

		String outputDirectory = suiteConfig.getOutputDirectory();
		runTestNg(xmlSuite, outputDirectory);
	}

	protected XmlSuite getXmlSuite(String suiteFile) {

		Collection<XmlSuite> suites;
		try {
			File xmlFile = SpringUtil.getClasspathFile(suiteFile);

			InputStream suiteFileInputStream = new FileInputStream(xmlFile);
			suites = new Parser(suiteFileInputStream).parse();
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Exception reading suite file '%s'", suiteFile));
		}

		ArrayList<XmlSuite> xmlSuites = new ArrayList<XmlSuite>(suites);
		if (xmlSuites.size() > 1) {
			throw new UnsupportedOperationException("Many xmlSuites not supported " + xmlSuites);
		}

		return xmlSuites.get(0);
	}

	protected void runTestNg(XmlSuite suite, String outputDirectory) {

		TestNG testng = new TestNG();
		testng.setXmlSuites(Collections.singletonList(suite));

		if (outputDirectory != null) {
			testng.setOutputDirectory(outputDirectory);
		}

		testng.run();
	}
}
