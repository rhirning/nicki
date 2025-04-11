
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


import java.io.IOException;
import java.util.Collection;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Interface NickiSearch.
 *
 * @param <T> the generic type
 */
public interface NickiSearch<T> {

	/** The attribute category. */
	//String ATTRIBUTE_HASH = "hash";
	String ATTRIBUTE_CATEGORY = "category";
	
	/** The attribute key. */
	String ATTRIBUTE_KEY = "key";
	
	/** The attribute title. */
	String ATTRIBUTE_TITLE = "title";
	
	/** The attribute description. */
	String ATTRIBUTE_DESCRIPTION = "description";
	
	/** The attribute content. */
	String ATTRIBUTE_CONTENT = "content";
	
	/**
	 * Sets the index path.
	 *
	 * @param property the new index path
	 */
	void setIndexPath(String property);
	
	/**
	 * Index.
	 *
	 * @param values the values
	 * @param xtractor the xtractor
	 */
	void index(Collection<T> values, Extractor<T> xtractor);
	
	/**
	 * Search.
	 *
	 * @param searchString the search string
	 * @param maxCount the max count
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List<NickiSearchResult> search(String searchString, int maxCount) throws IOException;
}
