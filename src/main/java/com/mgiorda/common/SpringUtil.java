package com.mgiorda.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;

public final class SpringUtil {

	private static final Log logger = LogFactory.getLog(SpringUtil.class);

	private SpringUtil() {

	}

	public static File getClasspathFile(String fileProperty) {

		File classpathFile = new File(fileProperty);
		if (!classpathFile.exists()) {

			@SuppressWarnings("resource")
			ApplicationContext appContext = new ClassPathXmlApplicationContext();
			Resource resource = appContext.getResource("classpath:" + fileProperty);

			try {
				File parentFile = classpathFile.getParentFile();
				if (parentFile != null) {
					parentFile.mkdirs();
				}
				classpathFile.createNewFile();
				InputStream inputStream = resource.getInputStream();

				FileOutputStream outputStream = new FileOutputStream(classpathFile);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				outputStream.close();

			} catch (IOException e) {
				throw new IllegalStateException(String.format("Exception getting Classpath file '%s'", fileProperty), e);
			}
		}

		return classpathFile;
	}

	public static void autowireBean(ApplicationContext applicationContext, Object bean) {

		try {

			AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
			beanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
			beanFactory.initializeBean(bean, bean.getClass().getName());

		} catch (Exception ex) {
			logger.warn(String.format("Exception autowiring '%s'", bean.getClass()), ex);

			throw ex;
		}
	}

	public static void addPropertiesFile(ApplicationContext applicationContext, String propertySource) {

		Properties properties = new Properties();

		propertySource = applicationContext.getEnvironment().resolvePlaceholders(propertySource);

		try {
			Resource resource = applicationContext.getResource("classpath:/" + propertySource);
			properties.load(resource.getInputStream());

			addProperties(applicationContext, properties);

		} catch (IOException e) {
			throw new IllegalStateException("Exception reading PropertySource test annotation", e);
		}
	}

	public static void addProperties(ApplicationContext applicationContext, Properties properties) {

		ConfigurableEnvironment env = (ConfigurableEnvironment) applicationContext.getEnvironment();
		MutablePropertySources sources = env.getPropertySources();
		sources.addLast(new PropertiesPropertySource("test-property-" + sources.size(), properties));

	}

}
