package com.mgiorda.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.xml.XmlSuite;

import com.mgiorda.context.run.SuiteConfiguration;
import com.mgiorda.testng.CurrentTestRun;

public abstract class AbstractTest extends com.mgiorda.testng.AbstractTest {

    private static final Log LOGGER = LogFactory.getLog(AbstractTest.class);

    private ApplicationContext applicationContext;

    @BeforeClass
    public void $beforeClassAutowireSpring() {

        try {

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

        } catch (Exception e) {

            LOGGER.warn(String.format("Exception autowiring beforeClass in test %s", this.getClass().getName()), e);
            throw e;
        }
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
