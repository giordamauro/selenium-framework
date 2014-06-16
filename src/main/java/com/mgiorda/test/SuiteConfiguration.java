package com.mgiorda.test;

import com.mgiorda.page.Browser;

public class SuiteConfiguration {

	private final String suiteXml;

	private final Browser browser;

	private int wailtTimeOut = 60; // Default value set to 60 seconds.

	private String outputDirectory = null;

	private String[] propertyFiles;

	public SuiteConfiguration(String suiteXml, Browser browser) {
		this.suiteXml = suiteXml;
		this.browser = browser;
	}

	public int getWailtTimeOut() {
		return wailtTimeOut;
	}

	public void setWailtTimeOut(int wailtTimeOut) {
		this.wailtTimeOut = wailtTimeOut;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String[] getPropertyFiles() {
		return propertyFiles;
	}

	public void setPropertyFiles(String[] propertyFiles) {
		this.propertyFiles = propertyFiles;
	}

	public String getSuiteXml() {
		return suiteXml;
	}

	public Browser getBrowser() {
		return browser;
	}
}
