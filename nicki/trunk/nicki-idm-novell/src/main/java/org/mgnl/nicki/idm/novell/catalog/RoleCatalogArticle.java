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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.idm.novell.objects.IdmPerson;
import org.mgnl.nicki.idm.novell.objects.Role;
import org.mgnl.nicki.shop.inventory.InventoryArticle;
import org.mgnl.nicki.shop.inventory.InventoryAttribute;
import org.mgnl.nicki.shop.objects.Catalog;
import org.mgnl.nicki.shop.objects.CatalogArticle;


public class RoleCatalogArticle extends CatalogArticle {

	private static final long serialVersionUID = -5530389061310235734L;
	public static final String ATTRIBUTE_ROLE = "role";
	@Override
	public void initDataModel() {
		super.initDataModel();
		addObjectClass("nickiRoleArticle");

		DynamicAttribute dynAttribute = new ReferenceDynamicAttribute(Role.class, ATTRIBUTE_ROLE, "nickiRoleRef", String.class,
				Config.getProperty("nicki.roles.basedn"));
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);

	}


	@Override
	public List<InventoryArticle> getInventoryArticles(Person person) {
		List<InventoryArticle> inventoryArticles = new ArrayList<InventoryArticle>();
		List<Role> roles = ((IdmPerson)person).getRoles();
		List<CatalogArticle> articles = Catalog.getCatalog().getAllArticles();
		for (CatalogArticle catalogArticle : articles) {
			if (RoleCatalogArticle.class.isAssignableFrom(catalogArticle.getClass())) {
				RoleCatalogArticle roleCatalogArticle = (RoleCatalogArticle) catalogArticle;
				for (Role role : roles) {
					if (roleCatalogArticle.contains(role)) {
						String articleId = catalogArticle.getCatalogPath();
						Map<String, String> attributeMap = person.getCatalogAttributes(articleId, null);
						List<InventoryAttribute> attributes = getInventoryAttributes(catalogArticle, attributeMap);
						inventoryArticles.add(new InventoryArticle(catalogArticle, role.getStartTime(),
								role.getEndTime(), attributes));
					}
				}
			}
		}
		return inventoryArticles;
	}

	public boolean contains(Role role) {
		if (getRole() != null && StringUtils.equalsIgnoreCase(role.getPath(), getRole().getPath())) {
			return true;
		}
		return false;
	}

	public Role getRole() {
		return getForeignKeyObject(Role.class, ATTRIBUTE_ROLE);
	}

}
