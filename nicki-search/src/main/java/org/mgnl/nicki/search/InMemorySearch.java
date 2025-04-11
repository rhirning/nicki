
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class InMemorySearch.
 *
 * @param <T> the generic type
 */
@Slf4j
public class InMemorySearch<T extends Object> implements Serializable, NickiSearch<T> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8131864934307700845L;
	
	/** The data. */
	private Map<String, String> data = new HashMap<String, String>();


	/**
	 * Instantiates a new in memory search.
	 */
	public InMemorySearch() {
	}

	/**
	 * Search.
	 *
	 * @param searchString the search string
	 * @param maxResults the max results
	 * @return the list
	 */
	public List<NickiSearchResult> search(String searchString, int maxResults) {
		String[] tokens = StringUtils.split(searchString);
		List<NickiSearchResult> list = new ArrayList<>();
		int count = 0;
		for (String key : data.keySet()) {
			boolean ok = true;
			for (String token : tokens) {
				if (!StringUtils.containsIgnoreCase(data.get(key), token)) {
					ok = false;
					break;
				}
			}
			if (ok) {
				String[] parts = StringUtils.split(key, ":");
				list.add(new NickiSearchResult(parts[0], parts[1], 1.0f));
				count++;
				if (maxResults > -1 && count >= maxResults) {
					break;
				}
			}
		}
		
		return list;
	}

	/**
	 * Index objects.
	 *
	 * @param list the list
	 * @param extractor the extractor
	 */
	private void indexObjects(Collection<T> list, Extractor<T> extractor) {
		if (list != null) {
			for (T t : list) {
				try {
					indexObject(t, extractor);
				} catch (IOException e) {
					log.error("Error indexing", e);
				}
			}
		}

	}

	/**
	 * Index object.
	 *
	 * @param object the object
	 * @param extractor the extractor
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void indexObject(T object, Extractor<T> extractor) throws IOException {
		if (!extractor.accept(object)) {
			return;
		}
		String category = extractor.getCategory(object);
		String key = extractor.getKey(object);
		String description = extractor.getDescription(object);
		String title = extractor.getTitle(object);
		StringBuilder sb = new StringBuilder(key);
		if (StringUtils.isNotBlank(title)) {
			sb.append(" ").append(title);
		}
		if (StringUtils.isNotBlank(description)) {
			sb.append(" ").append(description);
		}

		this.data.put(category + ":" + key, sb.toString());
	}

	/**
	 * Index.
	 *
	 * @param list the list
	 * @param extractor the extractor
	 */
	public void index(Collection<T> list, Extractor<T> extractor) {
		Date start = new Date();
		indexObjects(list, extractor);
		Date end = new Date();
		log.info(end.getTime() - start.getTime() + " total milliseconds");

	}

	/**
	 * Sets the index path.
	 *
	 * @param property the new index path
	 */
	@Override
	public void setIndexPath(String property) {
		// TODO Auto-generated method stub
		
	}

}
