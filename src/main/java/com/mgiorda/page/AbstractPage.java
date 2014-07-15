package com.mgiorda.page;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.mgiorda.context.ContextUtil;
import com.mgiorda.context.SpringUtil;
import com.mgiorda.context.SuiteContexts;
import com.mgiorda.page.annotations.PageURL;
import com.mgiorda.page.injector.PageElementValueRetriever;
import com.mgiorda.page.injector.PageValueRetriever;
import com.mgiorda.page.injector.ValueRetriever;
import com.mgiorda.testng.AbstractTest;
import com.mgiorda.testng.CurrentTestRun;
import com.mgiorda.testng.TestEventDispatcher;
import com.mgiorda.testng.TestSubscriber;

public class AbstractPage implements TestSubscriber {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private ApplicationContext applicationContext;

    private final PageHandlerFactory pageHandlerFactory;
    private final ElementInjector elementInjector;

    private final String pageUrl;

    private final DriverActionHandler actionHandler;
    protected final PageElementHandler pageHandler;

    protected AbstractPage(String url) {

        XmlSuite xmlSuite = CurrentTestRun.getXmlSuite();
        if (xmlSuite != null) {
            applicationContext = SuiteContexts.getContextForSuite(xmlSuite);
        }
        if (applicationContext == null) {
            throw new IllegalStateException("Missing applicationContext - Not running under test class?");
        }
        ContextUtil.initContext(applicationContext, this);

        this.pageHandlerFactory = applicationContext.getBean("pageHandlerFactory", PageHandlerFactory.class);
        this.elementInjector = applicationContext.getBean("elementInjector", ElementInjector.class);

        TestEventDispatcher testEventDispatcher = TestEventDispatcher.getEventDispatcher();
        testEventDispatcher.subscribe(this);

        this.actionHandler = pageHandlerFactory.getActionHandler();
        this.pageHandler = pageHandlerFactory.getElementHandler(this);

        this.pageUrl = getPageUrl(url);
        if (pageUrl == null) {
            throw new IllegalStateException("Cannot instantiate Page whitout String url constructor parameter or @PageURL class annotation");
        }

        logger.info(String.format("Browsing to url '%s'", pageUrl));
        actionHandler.goToUrl(pageUrl);

        ValueRetriever pageValueRetriever = new PageValueRetriever(pageHandler);
        elementInjector.autowireLocators(pageValueRetriever, this);
    }

    protected AbstractPage() {
        this((String) null);
    }

    protected AbstractPage(AbstractPage parentPage, String url) {

        this.applicationContext = parentPage.applicationContext;
        ContextUtil.initContext(applicationContext, this);

        this.actionHandler = parentPage.actionHandler;
        this.pageHandlerFactory = parentPage.pageHandlerFactory;
        this.elementInjector = parentPage.elementInjector;

        this.pageUrl = getPageUrl(url);
        this.pageHandler = pageHandlerFactory.getElementHandler(this);

        String currentUrl = actionHandler.getCurrentUrl();
        if (pageUrl != null && !pageUrl.equals(currentUrl)) {

            logger.info(String.format("Browsing to url '%s'", pageUrl));
            actionHandler.goToUrl(pageUrl);
        } else {
            actionHandler.waitForPageToLoad();
        }

        ValueRetriever pageValueRetriever = new PageValueRetriever(pageHandler);
        elementInjector.autowireLocators(pageValueRetriever, this);
    }

    protected AbstractPage(AbstractPage parentPage) {
        this(parentPage, (String) null);
    }

    AbstractPage(AbstractPage parentPage, PageElementHandler elementHandler) {

        this.actionHandler = parentPage.actionHandler;
        this.pageHandlerFactory = parentPage.pageHandlerFactory;
        this.elementInjector = parentPage.elementInjector;
        this.pageUrl = parentPage.pageUrl;

        this.pageHandler = elementHandler;

        this.applicationContext = parentPage.applicationContext;
        ContextUtil.initContext(applicationContext, this);

        ValueRetriever pageValueRetriever = new PageValueRetriever(pageHandler);
        elementInjector.autowireLocators(pageValueRetriever, this);
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

            String testName = testResult.getTestClass().getName() + " - " + testResult.getName();
            String outputDirectory = testResult.getTestContext().getOutputDirectory();

            takeScreenShot(outputDirectory, testName);
        }
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

    public ElementInjector getElementInjector() {
        return elementInjector;
    }

    protected void fetchElement(String fieldName) {

        ValueRetriever elementValueRetriever = new PageElementValueRetriever((AbstractElementHandler) pageHandler);
        elementInjector.autowireField(elementValueRetriever, this, fieldName);
    }

    private String getPageUrl(String url) {

        String pageUrl = url;

        if (pageUrl == null) {

            Class<?> pageClass = this.getClass();
            PageURL annotation = pageClass.getAnnotation(PageURL.class);
            if (annotation != null) {

                pageUrl = annotation.value();
            }
        }
        if (pageUrl != null) {
            pageUrl = SpringUtil.getPropertyPlaceholder(applicationContext, pageUrl);
        }
        return pageUrl;
    }

    private void takeScreenShot(String outputDirectory, String testName) {

        FastDateFormat fastDateFormat = FastDateFormat.getInstance("MM-dd-yyyy HH.mm.ss");
        String currentDate = null;
        // So that never would be two photos with same time stamp
        synchronized (this) {
            currentDate = fastDateFormat.format(new Date());
        }

        String browserName = actionHandler.getDriverName();
        String filePath = outputDirectory + "/fail-photos/" + browserName + String.format("/%s - %s.png", currentDate, testName);

        actionHandler.takeScreenShot(filePath);
    }
}
