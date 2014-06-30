package com.mgiorda.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AnnotationUtil {

	private static final Log logger = LogFactory.getLog(AnnotationUtil.class);

	private AnnotationUtil() {

	}

	public static <T> T getAnnotationValue(Class<?> targetClass, Class<? extends Annotation> annotationClass) {

		T result = null;

		Annotation annotation = targetClass.getAnnotation(annotationClass);
		if (annotation != null) {

			try {
				Method valueMethod = annotationClass.getDeclaredMethod("value");

				@SuppressWarnings("unchecked")
				T castedValue = (T) valueMethod.invoke(annotation);

				result = castedValue;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		logger.trace(String.format("Found value '%s' for annotation '%s' in class '%s'", result, annotationClass, targetClass));

		return result;
	}

}
