
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


import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.idm.novell.shop.objects.Resource;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogPage;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualResourceCatalogArticle.
 */
@SuppressWarnings("serial")
@DynamicObject
public class VirtualResourceCatalogArticle extends ResourceCatalogArticle {
	
	/** The resource. */
	private Resource resource;
	
	/** The page. */
	private CatalogPage page;

	/**
	 * Instantiates a new virtual resource catalog article.
	 *
	 * @param resource the resource
	 * @param page the page
	 */
	public VirtualResourceCatalogArticle(Resource resource, CatalogPage page) {
		this.resource = resource;
		this.page = page;
	}

	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return resource.getPath();
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	@Override
	public String getPath() {
		return resource.getPath();
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	@Override
	public String getDisplayName() {
		return resource.getDisplayName();
	}
	
	/**
	 * Gets the catalog path.
	 *
	 * @return the catalog path
	 */
	@Override
	public String getCatalogPath() {
		return page.getCatalogPath() + Catalog.PATH_SEPARATOR + getName();
	}


}
