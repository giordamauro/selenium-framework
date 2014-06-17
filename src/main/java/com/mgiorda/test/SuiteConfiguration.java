package com.mgiorda.test;

import java.util.Properties;

public class SuiteConfiguration {

	private final String suiteXml;

	private String outputDirectory = null;

	// private String[] locations;

	private Properties properties;

	public SuiteConfiguration(String suiteXml) {
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
}
