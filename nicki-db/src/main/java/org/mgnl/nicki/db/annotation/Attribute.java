
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

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Attribute {

	String name();

	boolean autogen() default false;
	
	String sequence() default "";

	boolean now() default false;

	boolean primaryKey() default false;
	
	DataType type() default DataType.DEFAULT;

	String editorClass() default "";
	
	boolean readonly() default false;

	boolean mandatory() default false;
	
	int length() default 0;
	
	String trim() default "";
}
