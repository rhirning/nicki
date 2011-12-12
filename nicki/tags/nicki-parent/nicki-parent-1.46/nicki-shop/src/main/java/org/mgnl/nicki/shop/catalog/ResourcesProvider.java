/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.shop.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Resource;

@SuppressWarnings("serial")
public class ResourcesProvider implements Provider, Serializable {
	CatalogPage page = null;

	public List<CatalogArticle> getArticles(CatalogPage catalogPage) {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		List<Resource> resources = catalogPage.getContext().loadObjects(Resource.class,
				Config.getProperty("nicki.resources.basedn"), null);
		if (resources != null && resources.size() > 0) {
			for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
				Resource resource = iterator.next();
				articles.add(new ResourceArticle(resource, catalogPage));
			}
		}
		return articles;
	}

	public CatalogArticle getArticle(String key) {
		Resource resource = this.page.getContext().loadObject(Resource.class, key);
		if (resource != null) {
			return new ResourceArticle(resource, this.page);
		}
		return null;
	}

	public void init(CatalogPage catalogPage) {
		this.page = catalogPage;
	}

}
