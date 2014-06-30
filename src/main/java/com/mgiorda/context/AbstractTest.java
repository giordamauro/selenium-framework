package com.mgiorda.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.testng.ISuite;
import org.testng.annotations.BeforeClass;
import org.testng.xml.XmlSuite;

import com.mgiorda.testng.CurrentTestRun;

public abstract class AbstractTest extends com.mgiorda.testng.AbstractTest {

	private ApplicationContext testAppContext;

	@BeforeClass
	public void $beforeClassAutowireSpring() {

		XmlSuite xmlSuite = getCurrentXmlSuite();
		if (xmlSuite != null) {
			testAppContext = SuiteContexts.getContextForSuite(xmlSuite);
		} else {
			String defaultContext = "classpath:/context/default-context.xml";
			testAppContext = new GenericXmlApplicationContext(defaultContext);
		}

		String[] locations = getContextLocations();
		if (locations.length != 0) {
			testAppContext = new ClassPathXmlApplicationContext(locations, testAppContext);
		}

		Properties defaultProperties = getDefaultProperties();
		if (defaultProperties != null) {
			SpringUtil.addProperties(testAppContext, defaultProperties);
		}

		addTestProperties();

		SpringUtil.autowireBean(testAppContext, this);
	}

	private XmlSuite getCurrentXmlSuite() {

		XmlSuite xmlSuite = null;

		ISuite suite = CurrentTestRun.getSuite();
		xmlSuite = suite.getXmlSuite();

		return xmlSuite;
	}

	private String[] getContextLocations() {

		String[] contextLocations = {};

		Class<?> testClass = this.getClass();
		Context annotation = testClass.getAnnotation(Context.class);
		if (annotation != null) {
			contextLocations = annotation.value();

			if (contextLocations.length == 0) {

				String path = testClass.getName().replaceAll("\\.", "/");
				String contextFile = "classpath*:" + path + "-context.xml";

				contextLocations = new String[] { contextFile };
			}
		}

		return contextLocations;
	}

	private Properties getDefaultProperties() {

		Properties properties = null;

		Class<?> testClass = this.getClass();

		InputStream resource = testClass.getResourceAsStream(testClass.getSimpleName() + ".properties");
		if (resource != null) {
			properties = new Properties();
			try {
				properties.load(resource);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		return properties;
	}

	private void addTestProperties() {

		Class<?> testClass = this.getClass();

		com.mgiorda.context.Properties annotation = testClass.getAnnotation(com.mgiorda.context.Properties.class);
		if (annotation != null) {

			String[] values = annotation.value();

			for (String propertySource : values) {
				SpringUtil.addPropertiesFile(testAppContext, propertySource);
			}
		}
	}
}
