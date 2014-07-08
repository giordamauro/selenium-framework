package com.mgiorda.context;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.testng.xml.XmlSuite;

public final class SuiteContexts {

	private static final Log logger = LogFactory.getLog(SuiteContexts.class);

	private static final Map<XmlSuite, ApplicationContext> suiteContexts = new IdentityHashMap<>();

	private SuiteContexts() {

	}

	public static synchronized void registerSuiteContext(XmlSuite suite, ApplicationContext context) {

		logger.debug(String.format("Registering '%s' suite context '%s'", suite, context));

		suiteContexts.put(suite, context);
	}

	public static ApplicationContext getContextForSuite(XmlSuite suite) {

		ApplicationContext context = suiteContexts.get(suite);

		return context;
	}
}
