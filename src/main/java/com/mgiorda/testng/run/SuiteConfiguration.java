package com.mgiorda.testng.run;

public class SuiteConfiguration {

	private final String file;
	private String outputDirectory = null;

	public SuiteConfiguration(String file) {
		this.file = file;
	}

	public String getFile() {
		return file;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

}
