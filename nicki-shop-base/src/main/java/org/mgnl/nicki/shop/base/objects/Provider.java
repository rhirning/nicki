
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


import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Interface Provider.
 */
public interface Provider {

	/**
	 * Gets the articles.
	 *
	 * @param catalogPage the catalog page
	 * @return the articles
	 */
	List<CatalogArticle> getArticles(CatalogPage catalogPage);

	/**
	 * Gets the article.
	 *
	 * @param key the key
	 * @return the article
	 */
	CatalogArticle getArticle(String key);

	/**
	 * Inits the.
	 *
	 * @param catalogPage the catalog page
	 */
	void init(CatalogPage catalogPage);
	
	/**
	 * Checks if is read only.
	 *
	 * @return true, if is read only
	 */
	boolean isReadOnly();

}
