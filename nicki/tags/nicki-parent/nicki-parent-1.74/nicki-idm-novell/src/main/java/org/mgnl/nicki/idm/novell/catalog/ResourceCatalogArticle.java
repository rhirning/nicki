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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.dynamic.objects.shop.Catalog;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticle;
import org.mgnl.nicki.idm.novell.objects.IdmPerson;
import org.mgnl.nicki.idm.novell.objects.Resource;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

public class ResourceCatalogArticle extends CatalogArticle {

	private static final long serialVersionUID = 3397169876567940029L;

	public static final String ATTRIBUTE_RESOURCE = "resource";
	private List<CatalogArticle> catalogArticles = null;;
	@Override
	public void initDataModel() {
		super.initDataModel();
		addObjectClass("nickiResourceArticle");
		
		DynamicAttribute dynAttribute = new ReferenceDynamicAttribute(Resource.class, ATTRIBUTE_RESOURCE, "nickiResourceRef", String.class,
				Config.getProperty("nicki.resources.basedn"));
		dynAttribute.setForeignKey(Resource.class);
		addAttribute(dynAttribute);

	}

	@Override
	public List<CatalogArticle> getArticles(Person person) {
		if (catalogArticles == null) {
			catalogArticles = new ArrayList<CatalogArticle>();
			List<Resource> resources = ((IdmPerson)person).getResources();
			List<CatalogArticle> articles = Catalog.getCatalog().getAllArticles();
			for (CatalogArticle catalogArticle : articles) {
				if (ResourceCatalogArticle.class.isAssignableFrom(catalogArticle.getClass())) {
					ResourceCatalogArticle resourceCatalogArticle = (ResourceCatalogArticle) catalogArticle;
					for (Resource resource : resources) {
						if (resourceCatalogArticle.contains(resource)) {
							catalogArticles.add(resourceCatalogArticle);
						}
					}
				}
			}
		}
		return catalogArticles;
	}

	public boolean contains(Resource resource) {
		if (StringUtils.equalsIgnoreCase(resource.getPath(), (String) get(ATTRIBUTE_RESOURCE))) {
			return true;
		}
		return false;
	}

	@Override
	public Date getStart(Person person) {
		Resource resource = getResource();
		if (resource != null) {
			return resource.getStartTime();
		}
		return null;
	}

	@Override
	public Date getEnd(Person person) {
		Resource resource = getResource();
		if (resource != null) {
			return resource.getEndTime();
		}
		return null;
	}

	@Override
	public String getSpecifier(Person person) {
		Resource resource = getResource();
		if (resource != null) {
			return resource.getParameter();
		}
		return null;
	}

	public Resource getResource() {
		return getForeignKeyObject(Resource.class, ATTRIBUTE_RESOURCE);
	}

}