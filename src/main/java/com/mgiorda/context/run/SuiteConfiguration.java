package com.mgiorda.context.run;

import java.util.Properties;

public class SuiteConfiguration extends com.mgiorda.testng.run.SuiteConfiguration {

	public static final String DEFAULT_CONTEXT_LOCATION = "contexts/default-context.xml";

	private String context = DEFAULT_CONTEXT_LOCATION;

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
