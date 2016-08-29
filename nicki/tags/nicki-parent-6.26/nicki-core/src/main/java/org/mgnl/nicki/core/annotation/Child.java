package org.mgnl.nicki.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mgnl.nicki.core.objects.DynamicObject;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Child {
	String name();
	String filter() default "";
	Class<? extends DynamicObject>[] objectFilter() default {};
}
