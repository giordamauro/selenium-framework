package com.mgiorda.context.run;

import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.xml.XmlSuite;

import com.mgiorda.context.SpringUtil;
import com.mgiorda.context.SuiteContexts;
import com.mgiorda.testng.run.FileSuiteRunner;

public class ContextSuiteRunner extends FileSuiteRunner<SuiteConfiguration> {

	@Override
	public void runSuite(SuiteConfiguration suiteConfig) {

		if (suiteConfig == null) {
			throw new IllegalStateException("Suite config cannot be null");
		}

		String contextFile = suiteConfig.getContext();
		ApplicationContext appContext = new GenericXmlApplicationContext(contextFile);

		Properties properties = suiteConfig.getProperties();
		if (properties != null) {
			SpringUtil.addProperties(appContext, properties);
		}

		String suiteFile = suiteConfig.getFile();
		XmlSuite xmlSuite = getXmlSuite(suiteFile);

		String nameSuffix = "";
		if (suiteConfig.getNameSuffix() != null) {
			nameSuffix = " - " + suiteConfig.getNameSuffix();
		}

		String suiteContextName = xmlSuite.getName() + String.format(" (%s)%s", contextFile.replaceAll("/", " "), nameSuffix);
		xmlSuite.setName(suiteContextName);

		SuiteContexts.registerSuiteContext(xmlSuite, appContext);

		String outputDirectory = suiteConfig.getOutputDirectory();
		runTestNg(xmlSuite, outputDirectory);
	}
}
