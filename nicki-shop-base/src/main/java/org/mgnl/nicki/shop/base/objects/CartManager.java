
package org.mgnl.nicki.shop.base.objects;

/*-
 * #%L
 * nicki-shop-base
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


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.shop.base.objects.CartEntry.ACTION;
import org.mgnl.nicki.shop.base.visitor.FindPermissionDnVisitor;

// TODO: Auto-generated Javadoc
/**
 * The Class CartManager.
 */
public class CartManager {
	
	/** The instance. */
	private static CartManager instance;
	
	/**
	 * Gets the catalog article.
	 *
	 * @param id the id
	 * @return the catalog article
	 */
	public CatalogArticle getCatalogArticle(String id) {
		return Catalog.getCatalog().getArticle(id);
	}
	
	/**
	 * Gets the permission dn.
	 *
	 * @param id the id
	 * @return the permission dn
	 */
	public String getPermissionDn(String id) {
		return getCatalogArticle(id).getPermissionDn();
	}
	
	/**
	 * Gets the catalog articles.
	 *
	 * @param permissionDn the permission dn
	 * @return the catalog articles
	 */
	public List<CatalogArticle> getCatalogArticles(String permissionDn) {
		FindPermissionDnVisitor visitor = new FindPermissionDnVisitor(permissionDn);
		Catalog.getCatalog().accept(visitor);
		return visitor.getCatalogArticles();
	}
	
	/**
	 * Gets the carts.
	 *
	 * @param personDn the person dn
	 * @param permissionDn the permission dn
	 * @param specifier the specifier
	 * @param cartEntryAction the cart entry action
	 * @param cartEntryStatus the cart entry status
	 * @return the carts
	 */
	public List<Cart> getCarts(String personDn, String permissionDn, String specifier,
			ACTION cartEntryAction, CartEntry.CART_ENTRY_STATUS cartEntryStatus) {
		StringBuilder query = new StringBuilder();
		
		for (CatalogArticle catalogArticle : getCatalogArticles(permissionDn)) {
			String cartEntryQualifier = catalogArticle.getId() + "#" + cartEntryAction + "#" + cartEntryStatus + "#";
			if (StringUtils.isNotBlank(specifier)) {
				cartEntryQualifier += specifier + "#*";
			}
			LdapHelper.addQuery(query, "nickiCartEntry=" + cartEntryQualifier + "*", LOGIC.OR);
		}
		LdapHelper.addQuery(query, "nickiRecipient=" + personDn, LOGIC.AND);

		return Catalog.getCatalog().getContext().loadObjects(Cart.class, Config.getString("nicki.carts.basedn"), query.toString());
	}
	
	/**
	 * Update carts.
	 *
	 * @param personDn the person dn
	 * @param permissionDn the permission dn
	 * @param specifier the specifier
	 * @param cartEntryAction the cart entry action
	 * @param oldCartEntryStatus the old cart entry status
	 * @param newCartEntryStatus the new cart entry status
	 * @param comment the comment
	 */
	public void updateCarts(String personDn, String permissionDn, String specifier, ACTION cartEntryAction,
			CartEntry.CART_ENTRY_STATUS oldCartEntryStatus, CartEntry.CART_ENTRY_STATUS newCartEntryStatus,
			String comment) {
		for (Cart cart : getCarts(personDn, permissionDn, specifier, cartEntryAction, oldCartEntryStatus)) {
			cart.updateStatus(permissionDn, specifier, cartEntryAction,
					oldCartEntryStatus, newCartEntryStatus, comment);
		}
		
		
		
	}
	
	/**
	 * Gets the cart manager.
	 *
	 * @return the cart manager
	 */
	public static CartManager getCartManager() {
		if (instance == null) {
			instance = new CartManager();
		}
		return instance;
	}
	
	

}
