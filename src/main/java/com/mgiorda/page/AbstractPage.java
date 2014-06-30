package com.mgiorda.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.xml.XmlSuite;

import com.mgiorda.annotation.PageURL;
import com.mgiorda.context.ContextUtil;
import com.mgiorda.context.SuiteContexts;

public class AbstractPage {

	protected final Log logger = LogFactory.getLog(this.getClass());

	private final ApplicationContext applicationContext;
	protected final DriverActionHandler actionHandler;

	private final String pageUrl;

	protected AbstractPage(String url) {

		XmlSuite xmlSuite = ContextUtil.getCurrentXmlSuite();
		if (xmlSuite != null) {
			applicationContext = SuiteContexts.getContextForSuite(xmlSuite);
		} else {
			String defaultContext = "classpath:/context/default-context.xml";
			applicationContext = new GenericXmlApplicationContext(defaultContext);
		}
		ContextUtil.initContext(applicationContext, this);

		this.actionHandler = applicationContext.getBean(DriverActionHandler.class);
		// Get PageElementHandlerFactory from context

		this.pageUrl = getPageUrl(url);

	}

	public String getPageUrl() {
		return pageUrl;
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	private String getPageUrl(String url) {

		String pageUrl = url;

		if (pageUrl == null) {

			Class<?> pageClass = this.getClass();
			PageURL annotation = pageClass.getAnnotation(PageURL.class);
			if (annotation == null) {
				throw new IllegalStateException("Cannot instantiate Page whitout String url constructor parameter or @PageURL class annotation");
			}

			pageUrl = annotation.value();
		}

		pageUrl = applicationContext.getEnvironment().resolvePlaceholders(pageUrl);

		return pageUrl;
	}
}
