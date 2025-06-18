
package org.mgnl.nicki.search;


/*-
 * #%L
 * nicki-search
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


/**
 * The Interface Extractor.
 *
 * @param <T> the generic type
 */
public interface Extractor<T extends Object> {
	
	/**
	 * Gets the category.
	 *
	 * @param object the object
	 * @return the category
	 */
	String getCategory(T object);
	
	/**
	 * Gets the key.
	 *
	 * @param object the object
	 * @return the key
	 */
	String getKey(T object);
	
	/**
	 * Gets the title.
	 *
	 * @param object the object
	 * @return the title
	 */
	String getTitle(T object);
	
	/**
	 * Gets the description.
	 *
	 * @param object the object
	 * @return the description
	 */
	String getDescription(T object);
	
	/**
	 * Accept.
	 *
	 * @param object the object
	 * @return true, if successful
	 */
	boolean accept(T object);
	
	
}
