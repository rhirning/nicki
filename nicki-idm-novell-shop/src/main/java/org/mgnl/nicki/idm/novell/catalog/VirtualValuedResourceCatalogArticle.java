
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
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.idm.novell.shop.objects.Resource;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogPage;
import org.mgnl.nicki.shop.base.objects.MultipleInstancesCatalogArticle;

@DynamicObject
@ObjectClass("nickiValuedResourceArticle")
public class VirtualValuedResourceCatalogArticle extends ValuedResourceCatalogArticle implements MultipleInstancesCatalogArticle {

	private static final long serialVersionUID = -7208705030668378943L;
	private Resource resource;
	private CatalogPage page;

	public VirtualValuedResourceCatalogArticle(Resource resource, CatalogPage page) {
		this.resource = resource;
		this.page = page;
	}
	
	public boolean isMultiple() {
		return true;
	}

	public Resource getResource() {
		return resource;
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
}
