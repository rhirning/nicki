
package org.mgnl.nicki.core.data;

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
import java.util.Map;



/**
 * The Interface Query.
 */
public interface Query {

	/**
	 * Adds the filter.
	 *
	 * @param filter the filter
	 */
	void addFilter(String filter);
	
	/**
	 * Adds the search value.
	 *
	 * @param key the key
	 * @param value the value
	 */
	void addSearchValue(String key, String value);
	
	/**
	 * Gets the search value.
	 *
	 * @param key the key
	 * @return the search value
	 */
	List<String> getSearchValue(String key);

	/**
	 * Gets the search values.
	 *
	 * @return the search values
	 */
	Map<String, List<String>> getSearchValues();
	
	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	String getFilter();
	
	/**
	 * Gets the base DN.
	 *
	 * @return the base DN
	 */
	String getBaseDN();

	/**
	 * Gets the result attributes.
	 *
	 * @return the result attributes
	 */
	Map<String, String> getResultAttributes();

	/**
	 * Sets the result attributes.
	 *
	 * @param resultAttributes the result attributes
	 */
	void setResultAttributes(Map<String, String> resultAttributes);
	
	/**
	 * Adds the result attribute.
	 *
	 * @param targetName the target name
	 * @param displayName the display name
	 */
	void addResultAttribute(String targetName, String displayName);


}
