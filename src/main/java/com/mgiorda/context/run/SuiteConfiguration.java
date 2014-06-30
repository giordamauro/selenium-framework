package com.mgiorda.context.run;

import java.util.Properties;

public class SuiteConfiguration extends com.mgiorda.testng.run.SuiteConfiguration {

	private String context = "context/default-context.xml";

	private Properties properties;

	public SuiteConfiguration(String file) {
		super(file);
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
		return properties;
	}

}
