package com.mgiorda.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class ContextUtil {

    private ContextUtil() {

    }

    public static void initContext(ApplicationContext applicationContext, Object target) {

        ApplicationContext baseApplicationContext = applicationContext;

        String[] locations = ContextUtil.getContextLocations(target.getClass());
        if (locations.length != 0) {
            baseApplicationContext = new ClassPathXmlApplicationContext(locations, applicationContext);
        }

        Properties defaultProperties = ContextUtil.getDefaultProperties(target.getClass());
        if (defaultProperties != null) {
            SpringUtil.addProperties(baseApplicationContext, defaultProperties);
        }

        ContextUtil.addPropertiesFromAnnotation(target.getClass(), baseApplicationContext);

        SpringUtil.autowireBean(baseApplicationContext, target);
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

}
