package org.mgnl.nicki.core.options;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * The Interface CommandLineOption.
 */
@Retention(RetentionPolicy.RUNTIME)  
@Repeatable(CommandLineOptions.class)
public @interface CommandLineOption {

	/**
	 * Option.
	 *
	 * @return the string
	 */
	String option();
	
	/**
	 * Attribute.
	 *
	 * @return the string
	 */
	String attribute();

	/**
	 * Long opt.
	 *
	 * @return the string
	 */
	String longOpt();

	/**
	 * Arg name.
	 *
	 * @return the string
	 */
	String argName() default "";

	/**
	 * Checks for arg.
	 *
	 * @return true, if successful
	 */
	boolean hasArg() default false;

	/**
	 * Desc.
	 *
	 * @return the string
	 */
	String desc();

	/**
	 * Bool.
	 *
	 * @return true, if successful
	 */
	boolean bool() default false;

}
