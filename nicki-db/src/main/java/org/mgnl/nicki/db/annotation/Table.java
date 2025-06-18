
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


/**
 * The Interface Table.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {

	/**
	 * Name.
	 *
	 * @return the string
	 */
	String name();

	/**
	 * Post init.
	 *
	 * @return the string
	 */
	String postInit() default "";
	
	/**
	 * Verify class.
	 *
	 * @return the class
	 */
	Class<?> verifyClass() default void.class;
	
	/**
	 * Update class.
	 *
	 * @return the class
	 */
	Class<?> updateClass() default void.class;
	
	/**
	 * Use prepared statement.
	 *
	 * @return true, if successful
	 */
	boolean usePreparedStatement() default true;
	
	/**
	 * Trim strings.
	 *
	 * @return the string
	 */
	String trimStrings() default "";
}
