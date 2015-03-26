/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.idm.novell.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.idm.novell.shop.objects.IdmPerson;
import org.mgnl.nicki.idm.novell.shop.objects.Role;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryAttribute;

@DynamicObject
@ObjectClass("nickiRoleArticle")
public class GroupRoleCatalogArticle extends RoleCatalogArticle {

	private static final long serialVersionUID = -4910554657631874729L;

	@Override
	public List<InventoryArticle> getInventoryArticles(Person person) {
		List<InventoryArticle> inventoryArticles = new ArrayList<InventoryArticle>();
		List<Role> roles = ((IdmPerson)person).getGroupRoles();
		List<CatalogArticle> articles = Catalog.getCatalog().getAllArticles();
		for (CatalogArticle catalogArticle : articles) {
			if (GroupRoleCatalogArticle.class.isAssignableFrom(catalogArticle.getClass())) {
				GroupRoleCatalogArticle roleCatalogArticle = (GroupRoleCatalogArticle) catalogArticle;
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

	@DynamicReferenceAttribute(externalName="nickiRoleRef", foreignKey=Role.class, reference=Role.class,
			baseProperty="nicki.roles.basedn")
	public Role getRole() {
		return getForeignKeyObject(Role.class, ATTRIBUTE_ROLE);
	}

}
