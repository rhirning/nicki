
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


public interface NickiSearch<T> {

	//String ATTRIBUTE_HASH = "hash";
	String ATTRIBUTE_CATEGORY = "category";
	String ATTRIBUTE_KEY = "key";
	String ATTRIBUTE_TITLE = "title";
	String ATTRIBUTE_DESCRIPTION = "description";
	String ATTRIBUTE_CONTENT = "content";
	void setIndexPath(String property);
	void index(Collection<T> values, Extractor<T> xtractor);
	List<NickiSearchResult> search(String searchString, int maxCount) throws IOException;
}
