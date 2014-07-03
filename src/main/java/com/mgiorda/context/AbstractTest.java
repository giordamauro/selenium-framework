package com.mgiorda.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.xml.XmlSuite;

import com.mgiorda.context.run.SuiteConfiguration;
import com.mgiorda.testng.CurrentTestRun;

public abstract class AbstractTest extends com.mgiorda.testng.AbstractTest {

	private ApplicationContext applicationContext;

	@BeforeClass
	public void $beforeClassAutowireSpring() {

		XmlSuite xmlSuite = CurrentTestRun.getXmlSuite();
		applicationContext = SuiteContexts.getContextForSuite(xmlSuite);

		if (applicationContext == null) {

			String defaultContext = "classpath*:/" + SuiteConfiguration.DEFAULT_CONTEXT_LOCATION;
			applicationContext = new GenericXmlApplicationContext(defaultContext);

			java.util.Properties defaultProperties = applicationContext.getBean("defaultProperties", java.util.Properties.class);
			SpringUtil.addProperties(applicationContext, defaultProperties);

			SuiteContexts.registerSuiteContext(xmlSuite, applicationContext);
		}

		ContextUtil.initContext(applicationContext, this);
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
