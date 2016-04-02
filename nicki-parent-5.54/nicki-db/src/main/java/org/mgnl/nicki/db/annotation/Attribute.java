package org.mgnl.nicki.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Attribute {

	String name();

	boolean autogen() default false;
	
	String sequence() default "";

	boolean foreignKey() default false;

	boolean now() default false;

	boolean primaryKey() default false;
}
