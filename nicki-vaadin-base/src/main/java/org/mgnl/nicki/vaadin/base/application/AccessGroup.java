package org.mgnl.nicki.vaadin.base.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AccessGroup {
	String[] name();
	Class<? extends AccessGroupEvaluator> evaluator() default DefaultGroupEvaluator.class;
}
