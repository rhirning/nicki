
package org.mgnl.nicki.shop.base.objects;

/*-
 * #%L
 * nicki-shop-base
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


import java.util.Map;


/**
 * The Interface CatalogValueProvider.
 */
public interface CatalogValueProvider {
	
	/**
	 * The Enum TYPE.
	 */
	enum TYPE {
/** The text area. */
TEXT_AREA, 
 /** The list. */
 LIST, 
 /** The input field. */
 INPUT_FIELD}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	TYPE getType();
	
	/**
	 * Gets the entries.
	 *
	 * @return the entries
	 */
	Map<String, String> getEntries();
	
	/**
	 * Check entry.
	 *
	 * @param entry the entry
	 * @return true, if successful
	 */
	boolean checkEntry(String entry);

	/**
	 * Checks if is only defined entries.
	 *
	 * @return true, if is only defined entries
	 */
	boolean isOnlyDefinedEntries();
	
	/**
	 * Inits the.
	 *
	 * @param article the article
	 */
	void init(CatalogArticle article);

}
