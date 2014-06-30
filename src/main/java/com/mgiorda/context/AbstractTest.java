package com.mgiorda.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.xml.XmlSuite;

public abstract class AbstractTest extends com.mgiorda.testng.AbstractTest {

	private ApplicationContext applicationContext;

	@BeforeClass
	public void $beforeClassAutowireSpring() {

		XmlSuite xmlSuite = ContextUtil.getCurrentXmlSuite();
		if (xmlSuite != null) {
			applicationContext = SuiteContexts.getContextForSuite(xmlSuite);
		} else {
			String defaultContext = "classpath:/context/default-context.xml";
			applicationContext = new GenericXmlApplicationContext(defaultContext);
		}

		ContextUtil.initContext(applicationContext, this);
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
