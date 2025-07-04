
package org.mgnl.nicki.idm.novell.catalog;

/*-
 * #%L
 * nicki-idm-novell-shop
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.idm.novell.shop.objects.Resource;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogPage;
import org.mgnl.nicki.shop.base.objects.Provider;


/**
 * The Class ResourcesProvider.
 */
@SuppressWarnings("serial")
public class ResourcesProvider implements Provider, Serializable {
	
	/** The page. */
	CatalogPage page = null;

	/**
	 * Gets the articles.
	 *
	 * @param catalogPage the catalog page
	 * @return the articles
	 */
	public List<CatalogArticle> getArticles(CatalogPage catalogPage) {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		String filter = page.getAttribute("providerData");
		List<Resource> resources = catalogPage.getContext().loadObjects(Resource.class,
				Config.getString("nicki.resources.basedn"), filter);
		if (resources != null && resources.size() > 0) {
			for (Resource resource : resources) {
				articles.add(new VirtualResourceCatalogArticle(resource, catalogPage));
			}
		}
		return articles;
	}

	/**
	 * Gets the article.
	 *
	 * @param key the key
	 * @return the article
	 */
	public CatalogArticle getArticle(String key) {
		Resource resource = this.page.getContext().loadObject(Resource.class, key);
		if (resource != null) {
			return new VirtualResourceCatalogArticle(resource, this.page);
		}
		return null;
	}

	/**
	 * Inits the.
	 *
	 * @param catalogPage the catalog page
	 */
	public void init(CatalogPage catalogPage) {
		this.page = catalogPage;
	}

	/**
	 * Checks if is read only.
	 *
	 * @return true, if is read only
	 */
	@Override
	public boolean isReadOnly() {
		return false;
	}

}
