
package org.mgnl.nicki.core.objects;

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


import java.util.List;


/**
 * The Interface ContextSearchResult.
 */
public interface ContextSearchResult {

	/**
	 * Gets the name in namespace.
	 *
	 * @return the name in namespace
	 */
	String getNameInNamespace();
	
	/**
	 * Gets the values.
	 *
	 * @param name the name
	 * @return the values
	 */
	List<Object> getValues(String name);

	/**
	 * Checks for attribute.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	boolean hasAttribute(String name);

	/**
	 * Gets the value.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @return the value
	 */
	Object getValue(Class<?> clazz, String name);

}
