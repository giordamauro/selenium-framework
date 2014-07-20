package com.mgiorda.context.run;

import java.util.Properties;

public class ContextsSuiteConfiguration extends com.mgiorda.testng.run.SuiteConfiguration {

	public static final String DEFAULT_CONTEXT_LOCATION = "contexts/default-context.xml";

	private String[] contexts = { DEFAULT_CONTEXT_LOCATION };

	private Properties properties;

	private String nameSuffix = null;

	public ContextsSuiteConfiguration(String file) {
		super(file);
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String[] getContexts() {
		return contexts;
	}

	public void setContexts(String[] contexts) {
		this.contexts = contexts;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getNameSuffix() {
		return nameSuffix;
	}

	public void setNameSuffix(String nameSuffix) {
		this.nameSuffix = nameSuffix;
	}
}
