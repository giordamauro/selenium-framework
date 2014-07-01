package com.mgiorda.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.xml.XmlSuite;

import com.mgiorda.testng.CurrentTestRun;

public abstract class AbstractTest extends com.mgiorda.testng.AbstractTest {

	private ApplicationContext applicationContext;

	@BeforeClass
	public void $beforeClassAutowireSpring() {

		XmlSuite xmlSuite = CurrentTestRun.getXmlSuite();
		if (xmlSuite != null) {
			applicationContext = SuiteContexts.getContextForSuite(xmlSuite);
		}
		if (applicationContext == null) {
			String defaultContext = "classpath:/context/default-context.xml";
			applicationContext = new GenericXmlApplicationContext(defaultContext);
		}

		ContextUtil.initContext(applicationContext, this);
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
