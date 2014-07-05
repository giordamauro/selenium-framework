package com.mgiorda.page.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Locate {

	By[] value();

	boolean optional() default false;
	
	boolean fetchOnInit() default true;
}
