package org.mgnl.nicki.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mgnl.nicki.core.objects.DynamicObject;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DynamicReferenceAttribute {
	boolean naming() default false;
	boolean mandatory() default false;
	boolean virtual() default false;
	boolean readonly() default false;
	String externalName();
	Class<? extends DynamicObject>[] foreignKey() default {};
	Class<? extends DynamicObject> reference();
	String baseProperty();
}
