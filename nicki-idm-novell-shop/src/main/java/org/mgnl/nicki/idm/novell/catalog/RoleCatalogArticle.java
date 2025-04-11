
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
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.idm.novell.shop.objects.IdmPerson;
import org.mgnl.nicki.idm.novell.shop.objects.Role;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

// TODO: Auto-generated Javadoc
/**
 * The Class RoleCatalogArticle.
 */
@DynamicObject
@ObjectClass("nickiRoleArticle")
public class RoleCatalogArticle extends CatalogArticle {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5530389061310235734L;
	
	/** The Constant ATTRIBUTE_ROLE. */
	public static final String ATTRIBUTE_ROLE = "role";

	
	/** The role. */
	@DynamicReferenceAttribute(externalName="nickiRoleRef", foreignKey=Role.class, reference=Role.class,
			baseProperty="nicki.roles.basedn")
	private String role;

	/**
	 * Gets the inventory articles.
	 *
	 * @param person the person
	 * @return the inventory articles
	 */
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
	
	

	/**
	 * Contains.
	 *
	 * @param role the role
	 * @return true, if successful
	 */
	public boolean contains(Role role) {
		if (getRole() != null && StringUtils.equalsIgnoreCase(role.getPath(), getRole().getPath())) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public Role getRole() {
		return getForeignKeyObject(Role.class, ATTRIBUTE_ROLE);
	}

	
	/**
	 * Gets the permission dn.
	 *
	 * @return the permission dn
	 */
	public String getPermissionDn() {
		return getAttribute(ATTRIBUTE_ROLE);
	}
	
	/**
	 * Gets the article type.
	 *
	 * @return the article type
	 */
	@Override
	public TYPE getArticleType() {
		return TYPE.ROLE;
	}

	/**
	 * Checks for article.
	 *
	 * @param person the person
	 * @return true, if successful
	 */
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
