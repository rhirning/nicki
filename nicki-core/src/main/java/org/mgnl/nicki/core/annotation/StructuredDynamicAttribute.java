
package org.mgnl.nicki.core.annotation;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mgnl.nicki.core.objects.DynamicObject;


/**
 * Defines a structured attribute.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface StructuredDynamicAttribute {
	
	/**
	 * Naming.
	 *
	 * @return true, if successful
	 */
	boolean naming() default false;
	
	/**
	 * Mandatory.
	 *
	 * @return true, if successful
	 */
	boolean mandatory() default false;
	
	/**
	 * Virtual.
	 *
	 * @return true, if successful
	 */
	boolean virtual() default false;
	
	/**
	 * Readonly.
	 *
	 * @return true, if successful
	 */
	boolean readonly() default false;
	
	/**
	 * External name.
	 *
	 * @return the string
	 */
	String externalName();
	
	/**
	 * Foreign key.
	 *
	 * @return the class<? extends dynamic object>[]
	 */
	Class<? extends DynamicObject>[] foreignKey() default {};
	
	/**
	 * Editor class.
	 *
	 * @return the string
	 */
	String editorClass() default "";
	
	/**
	 * Search field class.
	 *
	 * @return the string
	 */
	String searchFieldClass() default "";
	
	/**
	 * Search.
	 *
	 * @return true, if successful
	 */
	boolean search() default false;
	
	/**
	 * Caption.
	 *
	 * @return the string
	 */
	String caption() default "";
	
	/**
	 * Type.
	 *
	 * @return the class
	 */
	Class<?> type() default String.class;
	
	/**
	 * Creates the only.
	 *
	 * @return the org.mgnl.nicki.core.objects. dynamic attribute. CREATEONLY
	 */
	org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY createOnly()
	default org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY.FALSE;
}
