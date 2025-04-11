
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
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.idm.novell.shop.objects.IdmPerson;
import org.mgnl.nicki.idm.novell.shop.objects.Resource;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.mgnl.nicki.shop.base.objects.CatalogValueProvider;
import org.mgnl.nicki.shop.base.objects.MultipleInstancesCatalogArticle;
import org.mgnl.nicki.shop.base.objects.XmlValueProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class ValuedResourceCatalogArticle.
 */
@DynamicObject
@ObjectClass("nickiValuedResourceArticle")
public class ValuedResourceCatalogArticle extends ResourceCatalogArticle implements MultipleInstancesCatalogArticle {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7208705030668378943L;
	
	/** The Constant ATTRIBUTE_REQUEST_RESOURCE. */
	public static final String ATTRIBUTE_REQUEST_RESOURCE = "requestResource";
	
	/** The provider. */
	private CatalogValueProvider provider;

	/**
	 * Gets the provider data.
	 *
	 * @return the provider data
	 */
	@DynamicAttribute(externalName="nickiProviderData")
	public String getProviderData() {
		return getAttribute("providerData");
	}

	/**
	 * Checks if is multiple.
	 *
	 * @return true, if is multiple
	 */
	public boolean isMultiple() {
		return true;
	}

	/**
	 * Gets the provider.
	 *
	 * @return the provider
	 */
	@DynamicAttribute(externalName="nickiProvider")
	public String getProvider() {
		return getAttribute("provider");
	}

	/**
	 * Gets the catalog value provider.
	 *
	 * @return the catalog value provider
	 */
	public CatalogValueProvider getCatalogValueProvider() {
		if (provider == null) {
			try {
				provider = (CatalogValueProvider)Classes.newInstance(getProvider());
				provider.init(this);
				return provider;
			} catch (Exception e) {
				provider = new XmlValueProvider();
				provider.init(this);				
			}
		}
		return provider;
	}
	
	/**
	 * Gets the inventory articles.
	 *
	 * @param person the person
	 * @return the inventory articles
	 */
	@Override
	public List<InventoryArticle> getInventoryArticles(Person person) {
		List<InventoryArticle> inventoryArticles = new ArrayList<InventoryArticle>();

		for (Resource resource : ((IdmPerson)person).getResources()) {
			if (contains(resource)) {
				String specifier = resource.getParameter();
				inventoryArticles.add(new InventoryArticle(this, specifier, resource.getStartTime(),
						resource.getEndTime()));
			}
		}
		return inventoryArticles;
	}

	/**
	 * Checks for article.
	 *
	 * @param person the person
	 * @return true, if successful
	 */
	@Override
	public boolean hasArticle(Person person) {
		for (Resource resource : ((IdmPerson)person).getResources()) {
			if (contains(resource)) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Gets the request resource.
	 *
	 * @return the request resource
	 */
	@DynamicReferenceAttribute(externalName="nickiRequestResourceRef", foreignKey=Resource.class, reference=Resource.class,
			baseProperty="nicki.resources.basedn")
	public Resource getRequestResource() {
		return getForeignKeyObject(Resource.class, ATTRIBUTE_REQUEST_RESOURCE);
	}



}
