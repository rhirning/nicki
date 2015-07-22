package org.mgnl.nicki.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mgnl.nicki.core.objects.DynamicObject;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface StructuredDynamicAttribute {
	boolean naming() default false;
	boolean mandatory() default false;
	boolean virtual() default false;
	boolean readonly() default false;
	String externalName();
	Class<? extends DynamicObject>[] foreignKey() default {};
	String editorClass() default "";
	String searchFieldClass() default "";
	boolean search() default false;
	String caption() default "";
	Class<?> type() default String.class;
	org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY createOnly()
	default org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY.FALSE;
}
