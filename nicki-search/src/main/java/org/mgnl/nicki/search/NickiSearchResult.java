
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


public class NickiSearchResult {
	private String category;
	private String key;
	private String title;
	private String description;
	private float score;


	public NickiSearchResult(SearchDocument doc, float score) {
		this.category = doc.get(NickiSearch.ATTRIBUTE_CATEGORY);
		this.key = doc.get(NickiSearch.ATTRIBUTE_KEY);
		this.description = doc.get(NickiSearch.ATTRIBUTE_DESCRIPTION);
		this.title = doc.get(NickiSearch.ATTRIBUTE_TITLE);
		this.score = score;
	}


	public NickiSearchResult(String category, String key, float score) {
		this.category = category;
		this.key = key;
		this.score = score;
	}


	public String getKey() {
		return key;
	}


	public String getDescription() {
		return description;
	}


	public float getScore() {
		return score;
	}


	public String getTitle() {
		return title;
	}


	public String getCategory() {
		return category;
	}

}
