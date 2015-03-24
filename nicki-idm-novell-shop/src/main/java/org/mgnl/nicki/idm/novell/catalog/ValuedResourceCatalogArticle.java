/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.idm.novell.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.idm.novell.shop.objects.IdmPerson;
import org.mgnl.nicki.idm.novell.shop.objects.Resource;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryAttribute;
import org.mgnl.nicki.shop.base.objects.CatalogValueProvider;
import org.mgnl.nicki.shop.base.objects.MultipleInstancesCatalogArticle;
import org.mgnl.nicki.shop.base.objects.XmlValueProvider;

@DynamicObject
@ObjectClass("nickiValuedResourceArticle")
public class ValuedResourceCatalogArticle extends ResourceCatalogArticle implements MultipleInstancesCatalogArticle {

	private static final long serialVersionUID = -7208705030668378943L;
	private CatalogValueProvider provider = null;

	@DynamicAttribute(externalName="nickiProvider")
	private String providerClass;	
	@DynamicAttribute(externalName="nickiProviderData")
	private String providerData;

	public boolean isMultiple() {
		return true;
	}

	public String getValueProviderClass() {
		return getAttribute("providerClass");
	}

	public CatalogValueProvider getValueProvider() {
		if (provider == null) {
			try {
				provider = (CatalogValueProvider)Classes.newInstance(getValueProviderClass());
				provider.init(this);
				return provider;
			} catch (Exception e) {
				provider = new XmlValueProvider();
				provider.init(this);				
			}
		}
		return provider;
	}
	
	@Override
	public List<InventoryArticle> getInventoryArticles(Person person) {
		List<InventoryArticle> inventoryArticles = new ArrayList<InventoryArticle>();
		List<Resource> resources = ((IdmPerson)person).getResources();
		List<CatalogArticle> articles = Catalog.getCatalog().getAllArticles();
		for (CatalogArticle catalogArticle : articles) {
			if (ValuedResourceCatalogArticle.class.isAssignableFrom(catalogArticle.getClass())) {
				ValuedResourceCatalogArticle resourceCatalogArticle = (ValuedResourceCatalogArticle) catalogArticle;
				for (Resource resource : resources) {
					if (resourceCatalogArticle.contains(resource)) {
						String articleId = catalogArticle.getCatalogPath();
						String specifier = resource.getParameter();
						Map<String, String> attributeMap = person.getCatalogAttributes(articleId, specifier);
						List<InventoryAttribute> attributes = getInventoryAttributes(catalogArticle, attributeMap);
						inventoryArticles.add(new InventoryArticle(catalogArticle, resource.getStartTime(),
								resource.getEndTime(), attributes));
					}
				}
			}
		}
		return inventoryArticles;
	}



}
