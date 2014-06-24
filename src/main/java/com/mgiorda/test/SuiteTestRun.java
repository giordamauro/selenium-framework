package com.mgiorda.test;

import java.util.Map.Entry;
import java.util.Properties;

import com.mgiorda.page.Browser;

public class SuiteTestRun {

	private String file;

	private String context = "context/page-context.xml";

	private Browser browser;

	private int waitTimeOut = 60;

	private String outputDirectory = null;

	private Properties properties;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public int getWaitTimeOut() {
		return waitTimeOut;
	}

	public void setWaitTimeOut(int waitTimeOut) {
		this.waitTimeOut = waitTimeOut;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Properties getProperties() {

		Properties suiteProperties = new Properties();

		for (Entry<Object, Object> property : properties.entrySet()) {
			suiteProperties.put("suite." + property.getKey(), property.getValue());
		}

		suiteProperties.put("suite.file", file);
		suiteProperties.put("suite.waitTimeOut", waitTimeOut);
		suiteProperties.put("suite.browser", browser);
		suiteProperties.put("suite.context", context);

		String outputDir = getOutputDirectory();
		if (outputDir == null) {
			outputDir = "default";
		}
		suiteProperties.put("suite.outputDirectory", outputDir);

		return suiteProperties;
	}
}
