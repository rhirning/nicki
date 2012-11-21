package org.mgnl.nicki.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DynamicAttribute {
	boolean naming() default false;
	boolean multiple() default false;
	boolean mandatory() default false;
	boolean fix() default false;
	boolean virtual() default false;
	String externalName();
	String localName();
	Class<?> attributeClass() default String.class;
	

}
