
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


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.idm.novell.shop.objects.IdmPerson;
import org.mgnl.nicki.idm.novell.shop.objects.Resource;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

@DynamicObject
@ObjectClass("nickiResourceArticle")
public class ResourceCatalogArticle extends CatalogArticle {

	private static final long serialVersionUID = 3397169876567940029L;

	public static final String ATTRIBUTE_RESOURCE = "resource";

	@Override
	public List<InventoryArticle> getInventoryArticles(Person person) {
		List<InventoryArticle> inventoryArticles = new ArrayList<InventoryArticle>();

		for (Resource resource : ((IdmPerson)person).getResources()) {
			if (contains(resource)) {
				inventoryArticles.add(new InventoryArticle(this, resource.getStartTime(),
						resource.getEndTime()));
			}
		}
		return inventoryArticles;
	}

	public boolean contains(Resource resource) {
		if (getResource() != null && StringUtils.equalsIgnoreCase(resource.getPath(), getResource().getPath())) {
			return true;
		}
		return false;
	}

	
	@DynamicReferenceAttribute(externalName="nickiResourceRef", foreignKey=Resource.class, reference=Resource.class,
			baseProperty="nicki.resources.basedn")
	public Resource getResource() {
		return getForeignKeyObject(Resource.class, ATTRIBUTE_RESOURCE);
	}

	public Resource getRequestResource() {
		return null;
	}
	
	public boolean hasRequestResource() {
		return null != getRequestResource();
	}
	
	public String getPermissionDn() {
		return getAttribute(ATTRIBUTE_RESOURCE);
	}

	@Override
	public TYPE getArticleType() {
		return TYPE.RESOURCE;
	}
	


	@Override
	public boolean hasArticle(Person person) {
		for (Resource resource : ((IdmPerson)person).getResources()) {
			if (contains(resource)) {
				return true;
			}
		}
		return false;
	}

	
}
