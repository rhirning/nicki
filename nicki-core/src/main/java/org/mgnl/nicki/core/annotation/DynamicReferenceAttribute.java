/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mgnl.nicki.core.objects.DynamicObject;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DynamicReferenceAttribute {
	boolean naming() default false;
	boolean mandatory() default false;
	boolean virtual() default false;
	boolean readonly() default false;
	String externalName();
	Class<? extends DynamicObject>[] foreignKey() default {};
	Class<? extends DynamicObject> reference();
	String baseProperty();
	String editorClass() default "";
	String searchFieldClass() default "";
	boolean search() default false;
	String caption() default "";
	Class<?> type() default String.class;
	org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY createOnly()
	default org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY.FALSE;
}
