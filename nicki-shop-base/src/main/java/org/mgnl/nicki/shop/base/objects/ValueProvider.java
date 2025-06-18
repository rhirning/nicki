
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


import org.mgnl.nicki.dynamic.objects.objects.Person;


/**
 * The Interface ValueProvider.
 */
public interface ValueProvider {
	
	/**
	 * The Enum TYPE.
	 */
	public enum TYPE {
/** The self. */
SELF,
/** The all. */
ALL};

	/**
	 * Inits the.
	 *
	 * @param selector the selector
	 * @param i18nBase the i 18 n base
	 */
	void init(Selector selector, String i18nBase);

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	String getValue();

	/**
	 * Gets the person query.
	 *
	 * @param article the article
	 * @param value the value
	 * @return the person query
	 */
	String getPersonQuery(CatalogArticle article, String value);

	/**
	 * Gets the article query.
	 *
	 * @param person the person
	 * @param value the value
	 * @return the article query
	 */
	String getArticleQuery(Person person, Object value);

}
