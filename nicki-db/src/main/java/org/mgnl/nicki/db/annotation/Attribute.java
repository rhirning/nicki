
package org.mgnl.nicki.db.annotation;

/*-
 * #%L
 * nicki-db
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

import org.mgnl.nicki.db.data.DataType;

// TODO: Auto-generated Javadoc
/**
 * The Interface Attribute.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Attribute {

	/**
	 * Name.
	 *
	 * @return the string
	 */
	String name();

	/**
	 * Autogen.
	 *
	 * @return true, if successful
	 */
	boolean autogen() default false;
	
	/**
	 * Sequence.
	 *
	 * @return the string
	 */
	String sequence() default "";

	/**
	 * Now.
	 *
	 * @return true, if successful
	 */
	boolean now() default false;

	/**
	 * Primary key.
	 *
	 * @return true, if successful
	 */
	boolean primaryKey() default false;
	
	/**
	 * Type.
	 *
	 * @return the data type
	 */
	DataType type() default DataType.DEFAULT;

	/**
	 * Editor class.
	 *
	 * @return the string
	 */
	String editorClass() default "";
	
	/**
	 * Readonly.
	 *
	 * @return true, if successful
	 */
	boolean readonly() default false;

	/**
	 * Mandatory.
	 *
	 * @return true, if successful
	 */
	boolean mandatory() default false;
	
	/**
	 * Length.
	 *
	 * @return the int
	 */
	int length() default 0;
	
	/**
	 * Trim.
	 *
	 * @return the string
	 */
	String trim() default "";
}
