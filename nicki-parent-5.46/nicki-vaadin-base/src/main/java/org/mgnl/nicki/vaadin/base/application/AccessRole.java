package org.mgnl.nicki.vaadin.base.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AccessRole {
	String[] name();
	Class<? extends AccessRoleEvaluator> evaluator() default DefaultRoleEvaluator.class;
}
