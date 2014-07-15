package com.mgiorda.testng;

import java.util.HashMap;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public final class CurrentTestRun {

    private static final Map<Thread, ITestResult> testResults = new HashMap<>();
    private static final Map<Thread, AbstractTest> testInstances = new HashMap<>();
    private static final Map<Thread, ITestContext> testContexts = new HashMap<>();

    private CurrentTestRun() {
    }

    public static XmlSuite getXmlSuite() {

        XmlSuite xmlSuite = null;

        ISuite suite = CurrentTestRun.getSuite();
        if (suite != null) {
            xmlSuite = suite.getXmlSuite();
        }

        return xmlSuite;
    }

    public static ISuite getSuite() {

        ISuite suite = null;

        ITestContext context = getTestContext();
        if (context != null) {
            suite = context.getSuite();
        }

        return suite;
    }

    public static ITestContext getTestContext() {

        ITestContext context = null;

        ITestResult testResult = getTestResult();
        if (testResult != null) {
            context = testResult.getTestContext();
        } else {
            Thread thread = Thread.currentThread();
            context = testContexts.get(thread);
        }

        return context;
    }

    public static <T extends AbstractTest> T getTestInstance() {

        Object testInstance = null;

        ITestResult testResult = getTestResult();
        if (testResult != null) {
            testInstance = testResult.getInstance();
        } else {
            Thread thread = Thread.currentThread();
            testInstance = testInstances.get(thread);
        }

        @SuppressWarnings("unchecked")
        T castedInstance = (T) testInstance;

        return castedInstance;
    }

    public static ITestResult getTestResult() {

        Thread thread = Thread.currentThread();
        ITestResult testResult = testResults.get(thread);

        return testResult;
    }

    static synchronized void registerTestResult(ITestResult testResult) {

        Thread thread = Thread.currentThread();
        testResults.put(thread, testResult);
    }

    static synchronized void unregisterTestResult(ITestResult testResult) {

        Thread thread = Thread.currentThread();
        ITestResult iTestResult = testResults.get(thread);
        if (iTestResult != null && iTestResult.equals(testResult)) {
            testResults.remove(thread);
        }
    }

    static synchronized void registerTestInstance(AbstractTest testInstance) {

        Thread thread = Thread.currentThread();
        testInstances.put(thread, testInstance);
    }

    static synchronized void registerTestContext(ITestContext context) {

        Thread thread = Thread.currentThread();
        testContexts.put(thread, context);
    }

    static synchronized void unregisterTestContext(ITestContext testContext) {

        Thread thread = Thread.currentThread();

        ITestContext iTestContext = testContexts.get(thread);
        if (iTestContext != null && iTestContext.equals(testContext)) {
            testContexts.remove(thread);
        }
    }
}
