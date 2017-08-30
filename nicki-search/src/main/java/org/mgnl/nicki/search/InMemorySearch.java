/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemorySearch<T extends Object> implements NickiSearch<T> {
	private static final Logger LOG = LoggerFactory.getLogger(InMemorySearch.class);

	private Map<String, String> data = new HashMap<String, String>();


	public InMemorySearch() {
	}

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

	private void indexObjects(Collection<T> list, Extractor<T> extractor) {
		if (list != null) {
			for (T t : list) {
				try {
					indexObject(t, extractor);
				} catch (IOException e) {
					LOG.error("Error indexing", e);
				}
			}
		}

	}

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

	public void index(Collection<T> list, Extractor<T> extractor) {
		Date start = new Date();
		indexObjects(list, extractor);
		Date end = new Date();
		LOG.info(end.getTime() - start.getTime() + " total milliseconds");

	}

	@Override
	public void setIndexPath(String property) {
		// TODO Auto-generated method stub
		
	}

}
