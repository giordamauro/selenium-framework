package com.mgiorda.context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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

    public static File getCreateClasspathFile(String fileProperty) {

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

    public static File getClasspathFile(String fileProperty) {

        @SuppressWarnings("resource")
        ApplicationContext appContext = new ClassPathXmlApplicationContext();
        Resource resource = appContext.getResource("classpath:" + fileProperty);

        try {
            File file = resource.getFile();

            return file;
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Exception getting Classpath file '%s'", fileProperty), e);
        }
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

        propertySource = getPropertyPlaceholder(applicationContext, propertySource);

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

    public static String[] getContextLocations(Class<?> targetClass) {

        String[] contextLocations = {};

        Context annotation = targetClass.getAnnotation(Context.class);
        if (annotation != null) {
            contextLocations = annotation.value();

            if (contextLocations.length == 0) {

                String path = targetClass.getName().replaceAll("\\.", "/");
                String contextFile = "classpath*:" + path + "-context.xml";

                contextLocations = new String[] { contextFile };
            }
        }

        return contextLocations;
    }

    public static Properties getDefaultProperties(Class<?> targetClass) {

        Properties properties = null;

        InputStream resource = targetClass.getResourceAsStream(targetClass.getSimpleName() + ".properties");
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

    public static void addPropertiesFromAnnotation(Class<?> targetClass, ApplicationContext applicationContext) {

        com.mgiorda.context.Properties annotation = targetClass.getAnnotation(com.mgiorda.context.Properties.class);
        if (annotation != null) {

            String[] values = annotation.value();

            for (String propertySource : values) {
                SpringUtil.addPropertiesFile(applicationContext, propertySource);
            }
        }
    }

    public static String getPropertyPlaceholder(ApplicationContext applicationContext, String property) {

        String propertyValue = applicationContext.getEnvironment().resolvePlaceholders(property);

        if (propertyValue.matches(".*?\\$\\{(.*)\\}.*?")) {
            throw new IllegalStateException(String.format("Unsatisfied property-holder for property '%s'", property));
        }

        return propertyValue;
    }

    public static <T> T getPropertyPlaceholder(ApplicationContext applicationContext, String property, Class<T> propertyClass) {

        String propertyValue = getPropertyPlaceholder(applicationContext, property);

        try {
            Method method = propertyClass.getMethod("valueOf", String.class);

            @SuppressWarnings("unchecked")
            T returnValue = (T) method.invoke(null, propertyValue);

            return returnValue;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
