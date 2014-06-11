package com.mgiorda.testng;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({ SuiteLogger.class, TestLogger.class })
@ContextConfiguration("classpath:/testsContext.xml")
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

	@BeforeClass
	public void logBeforeClass() {
		logger.info(String.format("Initiating test Class '%s'", this.getClass().getSimpleName()));

		Class<?> testClass = this.getClass();
		PropertySource annotation = testClass.getAnnotation(PropertySource.class);
		if (annotation != null) {

			ConfigurableEnvironment env = (ConfigurableEnvironment) applicationContext.getEnvironment();
			String[] values = annotation.value();

			int propertyNumber = 1;
			for (String propertySource : values) {

				Properties properties = new Properties();

				try {
					Resource resource = applicationContext.getResource(propertySource);
					properties.load(resource.getInputStream());

					MutablePropertySources sources = env.getPropertySources();
					sources.addLast(new PropertiesPropertySource("test-property" + propertyNumber, properties));

					propertyNumber++;

				} catch (IOException e) {
					throw new IllegalStateException("Exception reading PropertySource test annotation", e);
				}
			}

			autowireBean(this);
		}
	}

	@AfterClass
	public void logAfterClass() {

		logger.info(String.format("Finishing test Class '%s'", this.getClass().getSimpleName()));
	}

	<T extends AbstractPage> void initPageContext(T page) {

		autowireBean(page);
		TestPoolManager.registerPage(page);
	}

	private void autowireBean(Object bean) {
		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
		beanFactory.initializeBean(bean, bean.getClass().getName());
	}

}
