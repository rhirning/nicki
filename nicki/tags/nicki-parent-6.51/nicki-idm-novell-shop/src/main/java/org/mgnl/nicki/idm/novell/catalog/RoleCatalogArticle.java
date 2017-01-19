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
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.idm.novell.shop.objects.IdmPerson;
import org.mgnl.nicki.idm.novell.shop.objects.Role;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

@DynamicObject
@ObjectClass("nickiRoleArticle")
public class RoleCatalogArticle extends CatalogArticle {

	private static final long serialVersionUID = -5530389061310235734L;
	public static final String ATTRIBUTE_ROLE = "role";

	
	@DynamicReferenceAttribute(externalName="nickiRoleRef", foreignKey=Role.class, reference=Role.class,
			baseProperty="nicki.roles.basedn")
	private String role;

	@Override
	public List<InventoryArticle> getInventoryArticles(Person person) {
		List<InventoryArticle> inventoryArticles = new ArrayList<InventoryArticle>();
		for (Role role : ((IdmPerson)person).getRoles()) {
			if (contains(role)) {
				inventoryArticles.add(new InventoryArticle(this, role.getStartTime(),
						role.getEndTime()));
			}
		}
		for (Role role : ((IdmPerson)person).getGroupRoles()) {
			if (contains(role)) {
				InventoryArticle iArticle = new InventoryArticle(this, role.getStartTime(),
						role.getEndTime());
				iArticle.setReadOnly(true);
				inventoryArticles.add(iArticle);
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

	
	public String getPermissionDn() {
		return getAttribute(ATTRIBUTE_ROLE);
	}
	
	@Override
	public TYPE getArticleType() {
		return TYPE.ROLE;
	}

	@Override
	public boolean hasArticle(Person person) {
		for (Role role : ((IdmPerson)person).getRoles()) {
			if (contains(role)) {
				return true;
			}
		}
		return false;
	}

}
