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

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Resource;

@SuppressWarnings("serial")
public class ResourceArticle extends CatalogArticle {
	private Resource resource;
	private CatalogPage page;

	public ResourceArticle(Resource resource, CatalogPage page) {
		this.resource = resource;
		this.page = page;
	}

	@Override
	public TYPE getArticleType() {
		return TYPE.RESOURCE;
	}

	@Override
	public List<CatalogArticleAttribute> getAttributes() {
		return new ArrayList<CatalogArticleAttribute>();
	}

	@Override
	public List<CatalogArticleAttribute> getInheritedAttributes() {
		return page.getAttributes();
	}

	@Override
	public List<CatalogArticleAttribute> getAllAttributes() {
		return getInheritedAttributes();
	}

	@Override
	public String getName() {
		return resource.getPath();
	}

	@Override
	public String getPath() {
		return resource.getPath();
	}

	@Override
	public String getDisplayName() {
		return resource.getDisplayName();
	}
	
	@Override
	public String getCatalogPath() {
		return page.getCatalogPath() + Catalog.PATH_SEPARATOR + getName();
	}

	@Override
	public String getResourcePath() {
		return resource.getPath();
	}


}
