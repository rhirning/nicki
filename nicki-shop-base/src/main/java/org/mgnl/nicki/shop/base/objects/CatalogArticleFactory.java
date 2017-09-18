
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


import java.util.HashMap;
import java.util.Map;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;

public class CatalogArticleFactory {
	private static CatalogArticleFactory instance;
	Map<Class<?>, CatalogArticle> articleTypes = new HashMap<Class<?>, CatalogArticle>();
	
	public static CatalogArticleFactory getInstance() throws InvalidPrincipalException, InstantiateDynamicObjectException {
		if (instance == null) {
			instance = new CatalogArticleFactory();
		}
		return instance;
	}

	public CatalogArticleFactory() throws InvalidPrincipalException, InstantiateDynamicObjectException {
		
		for (CatalogArticle catalogArticle : Catalog.getCatalog().getAllArticles()) {
			if (!articleTypes.containsKey(catalogArticle.getClass())) {
				try {
					articleTypes.put(catalogArticle.getClass(), catalogArticle.getClass().newInstance());
				} catch (Exception e) {
					articleTypes.put(catalogArticle.getClass(), catalogArticle);
				}
			}
		}
	}

	public Map<Class<?>, CatalogArticle> getArticleTypes() {
		return articleTypes;
	}

}
