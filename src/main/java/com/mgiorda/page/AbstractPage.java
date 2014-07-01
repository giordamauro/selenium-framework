package com.mgiorda.page;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.mgiorda.context.ContextUtil;
import com.mgiorda.context.SpringUtil;
import com.mgiorda.context.SuiteContexts;
import com.mgiorda.page.annotations.Locate;
import com.mgiorda.page.annotations.PageURL;
import com.mgiorda.testng.AbstractTest;
import com.mgiorda.testng.CurrentTestRun;
import com.mgiorda.testng.TestEventDispatcher;
import com.mgiorda.testng.TestSubscriber;

public class AbstractPage implements TestSubscriber {

	protected final Log logger = LogFactory.getLog(this.getClass());

	private ApplicationContext applicationContext;

	private final PageHandlerFactory pageHandlerFactory;

	private final String pageUrl;

	private final DriverActionHandler actionHandler;
	private final PageElementHandler elementHandler;

	protected AbstractPage(String url) {

		XmlSuite xmlSuite = CurrentTestRun.getXmlSuite();
		if (xmlSuite != null) {
			applicationContext = SuiteContexts.getContextForSuite(xmlSuite);
		}
		ContextUtil.initContext(applicationContext, this);

		this.pageHandlerFactory = applicationContext.getBean("pageHandlerFactory", PageHandlerFactory.class);

		this.actionHandler = pageHandlerFactory.getActionHandler();
		this.elementHandler = pageHandlerFactory.getElementHandler(this);
		this.pageUrl = getPageUrl(url);

		actionHandler.goToUrl(pageUrl);
		autowireLocators();

		TestEventDispatcher testEventDispatcher = TestEventDispatcher.getEventDispatcher();
		testEventDispatcher.subscribe(this);
	}

	protected AbstractPage() {
		this((String) null);
	}

	protected AbstractPage(AbstractPage parentPage, String url) {

		this.applicationContext = parentPage.applicationContext;
		ContextUtil.initContext(applicationContext, this);

		this.actionHandler = parentPage.actionHandler;
		this.pageHandlerFactory = parentPage.pageHandlerFactory;

		this.pageUrl = getPageUrl(url);
		this.elementHandler = pageHandlerFactory.getElementHandler(this);

		String currentUrl = actionHandler.getCurrentUrl();
		if (!pageUrl.equals(currentUrl)) {
			actionHandler.goToUrl(pageUrl);
		} else {
			actionHandler.waitForPageToLoad();
		}
		autowireLocators();
	}

	protected AbstractPage(AbstractPage parentPage) {
		this(parentPage, (String) null);
	}

	AbstractPage(AbstractPage parentPage, PageElementHandler elementHandler) {

		this.actionHandler = parentPage.actionHandler;
		this.pageHandlerFactory = parentPage.pageHandlerFactory;
		this.pageUrl = parentPage.pageUrl;

		this.elementHandler = elementHandler;

		this.applicationContext = parentPage.applicationContext;
		ContextUtil.initContext(applicationContext, this);

		autowireLocators();
	}

	@Override
	public void onClassStart(AbstractTest test) {
	}

	@Override
	public void onTestStart(ITestResult testResult) {
	}

	@Override
	public void onTestFinish(ITestResult testResult) {

		if (!testResult.isSuccess()) {

			String outputDirectory = testResult.getTestContext().getOutputDirectory();
			takeScreenShot(outputDirectory);
		}
		actionHandler.quit();
	}

	@Override
	public void onClassFinish(AbstractTest test) {
		actionHandler.quit();
	}

	public String getTitle() {
		return actionHandler.getTitle();
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public ApplicationContext getApplicationContext() {
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

		pageUrl = SpringUtil.getPropertyPlaceholder(applicationContext, pageUrl);

		return pageUrl;
	}

	private void autowireLocators() {

		Class<?> pageClass = this.getClass();

		Field[] declaredFields = pageClass.getDeclaredFields();
		for (Field field : declaredFields) {

			Locate annotation = field.getAnnotation(Locate.class);
			if (annotation != null) {

				Locator[] locators = AnnotationsUtil.getLocatorsFromAnnotation(annotation);
				if (locators.length == 0) {
					throw new IllegalStateException(String.format("Couldn't find an element locator for field '%s' in page class '%s'", field.getName(), pageClass.getSimpleName()));
				}

				Object value = getPageForField(field, locators);
				if (value == null) {
					value = AnnotationsUtil.getLocatorElementForHandler(field, elementHandler, locators);
				}

				if (value == null) {
					throw new IllegalStateException(String.format("Cannot autowire field '%s' of type '%s' in page '%s'", field.getName(), field.getType(), pageClass));
				}

				AnnotationsUtil.setField(this, field, value);
			}
		}
	}

	private AbstractPage getPageForField(Field field, Locator[] locators) {

		AbstractPage page = null;

		Class<?> fieldType = field.getType();

		if (AbstractPage.class.isAssignableFrom(fieldType)) {

			@SuppressWarnings("unchecked")
			Class<? extends AbstractPage> pageClass = (Class<? extends AbstractPage>) fieldType;

			page = elementHandler.getPageAs(pageClass, locators);
		}

		return page;
	}

	private void takeScreenShot(String outputDirectory) {

		Long currentTime = Long.valueOf(0);
		// So that never would be two photos with same time stamp
		synchronized (currentTime) {
			currentTime = new Date().getTime();
		}

		String browserName = actionHandler.getDriverName();
		String filePath = outputDirectory + File.separator + "fail-photos" + File.separator + browserName + File.separator + currentTime + ".png";

		actionHandler.takeScreenShot(filePath);
	}

}
