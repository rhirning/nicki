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
package org.mgnl.nicki.shop.inventory;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.shop.inventory.InventoryArticle.STATUS;
import org.mgnl.nicki.shop.objects.Cart;
import org.mgnl.nicki.shop.objects.CartEntry;
import org.mgnl.nicki.shop.objects.CartEntry.ACTION;
import org.mgnl.nicki.shop.objects.Catalog;
import org.mgnl.nicki.shop.objects.CatalogArticle;
import org.mgnl.nicki.shop.objects.CatalogArticleFactory;

@SuppressWarnings("serial")
public class Inventory implements Serializable {
	public enum SOURCE {
		SHOP, RULE, NONE;

		public String getValue() {
			return this.toString().toLowerCase();
		}

		public static SOURCE fromString(String str) {
			try {
				return SOURCE.valueOf(str.toUpperCase());
			} catch (Exception e) {
				return NONE;
			}
		}
	};

	private Person user;
	private Person person;
	
	private String cartPath = null;

	private Map<String, InventoryArticle> articles = new HashMap<String, InventoryArticle>();
	
	private Map<String, Map<String, InventoryArticle>> mulitArticles = new HashMap<String, Map<String, InventoryArticle>>();
	
	public Map<String, InventoryArticle> getArticles() {
		return articles;
	}

	public Inventory(Person user, Person person)
			throws InvalidPrincipalException, InstantiateDynamicObjectException {
		super();
		this.setUser(user);
		this.person = person;
		init();
	}

	private void init() throws InvalidPrincipalException, InstantiateDynamicObjectException {

		List<CatalogArticle> availableArticles = CatalogArticleFactory.getInstance().getArticles();
		for (CatalogArticle catalogArticle : availableArticles) {
			List<InventoryArticle> inventoryArticles = catalogArticle.getInventoryArticles(getPerson());
			for (InventoryArticle inventoryArticle : inventoryArticles) {
				if (catalogArticle.isMultiple()) {
					addArticle(inventoryArticle.getArticle(), inventoryArticle.getSpecifier(), inventoryArticle);
				} else {
					addArticle(inventoryArticle.getArticle(), inventoryArticle);
				}
			}
		}
	}
	
	public InventoryArticle addArticle(CatalogArticle catalogArticle) {
		if (!this.articles.containsKey(catalogArticle.getPath())) {
			InventoryArticle inventoryArticle = new InventoryArticle(catalogArticle);
			this.articles.put(catalogArticle.getPath(), inventoryArticle);
			return inventoryArticle;
		} else {
			return this.articles.get(catalogArticle.getPath());
		}
	}
	
	public void addArticle(InventoryArticle inventoryArticle) {
		if (inventoryArticle.getArticle().isMultiple()) {
			addArticle(inventoryArticle.getArticle(), inventoryArticle.getSpecifier(), inventoryArticle);
		} else {
			addArticle(inventoryArticle.getArticle(), inventoryArticle);
		}
	}

	
	private void addArticle(CatalogArticle catalogArticle, String specifier, InventoryArticle inventoryArticle) {
		Map<String, InventoryArticle> map = this.mulitArticles.get(catalogArticle.getPath());
		if (map == null) {
			map = new HashMap<String, InventoryArticle>();
			this.mulitArticles.put(catalogArticle.getPath(), map);
		}
		map.put(specifier, inventoryArticle);
	}

	private void addArticle(CatalogArticle catalogArticle, InventoryArticle inventoryArticle) {
		if (!this.articles.containsKey(catalogArticle.getPath())) {
			this.articles.put(catalogArticle.getPath(), inventoryArticle);
		}
	}

	public boolean hasArticle(CatalogArticle article) {
		return articles.containsKey(article.getPath());
	}

	public InventoryArticle getArticle(CatalogArticle article) {
		return articles.get(article.getPath());
	}

	public Map<String, InventoryArticle> getArticles(CatalogArticle article) {
		return mulitArticles.get(article.getPath());
	}

	public void addArticle(CatalogArticle catalogArticle, String specifier) {
		InventoryArticle iArticle = getInventoryArticle(catalogArticle, specifier);
		if (iArticle == null) {
			Map<String, InventoryArticle> map = this.mulitArticles.get(catalogArticle.getPath());
			if (map == null) {
				map = new HashMap<String, InventoryArticle>();
				this.mulitArticles.put(catalogArticle.getPath(), map);
			}
			map.put(specifier, new InventoryArticle(catalogArticle, specifier));
		} else if (iArticle.getStatus() == STATUS.DELETED) {
			iArticle.reset();
		}
	}
	
	public InventoryArticle getInventoryArticle(CatalogArticle catalogArticle) {
		return this.articles.get(catalogArticle.getPath());
	}
	
	public InventoryArticle getInventoryArticle(CatalogArticle catalogArticle, String specifier) {
		Map<String, InventoryArticle> map = this.mulitArticles.get(catalogArticle.getPath());
		if (map != null) {
			return map.get(specifier);
		}
		return null;
	}

	public void removeArticle(CatalogArticle catalogArticle, String specifier) {
		InventoryArticle iArticle = getInventoryArticle(catalogArticle, specifier);
		if (iArticle != null) {
			if (iArticle.getStatus() == STATUS.NEW) {
				removeArticle(catalogArticle.getPath(), specifier);
			} else {
				iArticle.setStatus(STATUS.DELETED);
			}
		}
	}

	public void removeArticle(CatalogArticle catalogArticle) {
		if (this.articles.containsKey(catalogArticle.getPath())) {
			this.articles.remove(catalogArticle.getPath());
		}
	}

	private void removeArticle(String path, String specifier) {
		Map<String, InventoryArticle> map = this.mulitArticles.get(path);
		if (map != null) {
			if (map.containsKey(specifier));
			map.remove(specifier);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Inventory for ").append(person.getDisplayName())
				.append("\n");
		for (String key : articles.keySet()) {
			sb.append(articles.get(key).toString()).append("\n");
		}
		return sb.toString();
	}

	public Cart getCart(String source, Cart.CART_STATUS cartStatus) {
		if (hasChanged()) {
			Cart cart;
			try {
				cart = person.getContext().getObjectFactory()
						.getDynamicObject(Cart.class);
				cart.getContext().getAdapter().initNew(cart, Config.getProperty("nicki.carts.basedn"),
						Long.toString(new Date().getTime()));
				cart.setInitiator(user);
				cart.setRecipient(person);
				cart.setRequestDate(new Date());
				cart.setCartStatus(cartStatus);
				cart.setSource(source);
				cart.setCatalog(Catalog.getCatalog());
				for (String key : mulitArticles.keySet()) {
					Map<String, InventoryArticle> list = mulitArticles.get(key);
					for (String specifier : list.keySet()) {
						InventoryArticle iArticle = list.get(specifier);
						if (iArticle.hasChanged()) {
							CartEntry entry = new CartEntry(
									iArticle.getArticle().getCatalogPath(), specifier,
									InventoryArticle.getAction(iArticle.getStatus()));
							entry.setStart(iArticle.getStart());
							entry.setEnd(iArticle.getEnd());
							for (InventoryAttribute iAttribute : iArticle.getAttributes().values()) {
								if (iAttribute.hasChanged()) {
									entry.addAttribute(iAttribute.getName(),
											iAttribute.getValue());
								}
							}
							cart.addCartEntry(entry);
						}
					}
				}
				for (String key : articles.keySet()) {
					InventoryArticle iArticle = articles.get(key);
					if (iArticle.hasChanged()) {
						CartEntry entry = new CartEntry(
								iArticle.getArticle().getCatalogPath(),
								InventoryArticle.getAction(iArticle.getStatus()));
						for (InventoryAttribute iAttribute : iArticle.getAttributes().values()) {
							if (iAttribute.hasChanged()) {
								entry.addAttribute(iAttribute.getName(),
										iAttribute.getValue());
							}
						}
						cart.addCartEntry(entry);
					}
				}
				return cart;
			} catch (InstantiateDynamicObjectException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Cart save(String source, Cart oldCart) throws InstantiateDynamicObjectException,
			DynamicObjectException {
		if (oldCart != null) {
			oldCart.delete();
		}
		if (hasChanged()) {
			Cart cart = getCart(source, Cart.CART_STATUS.NEW);
			cart.create();
			return cart;
		}
		return null;
	}

	public Cart remember(String source, Cart oldCart) throws InstantiateDynamicObjectException,
			DynamicObjectException {
		if (oldCart != null) {
			oldCart.delete();
		}
		if (hasChanged()) {
			Cart cart = getCart(source, Cart.CART_STATUS.TEMP);
			cart.create();
			return cart;
		}
		return null;
	}

	public boolean hasChanged() {
		for (String key : mulitArticles.keySet()) {
			Map<String, InventoryArticle> map = mulitArticles.get(key);
			for (String specifier : map.keySet()) {
				InventoryArticle iArticle = map.get(specifier);
				if (iArticle.hasChanged()) {
					return true;
				}
			}
		}
		for (String key : articles.keySet()) {
			InventoryArticle iArticle = articles.get(key);
			if (iArticle.hasChanged()) {
				return true;
			}
		}
		return false;
	}

	public void setUser(Person user) {
		this.user = user;
	}

	public Person getUser() {
		return user;
	}

	public Person getPerson() {
		return person;
	}

	public boolean hasArticle(CatalogArticle catalogArticle, String value) {
		if (!hasArticle(catalogArticle)) {
			return false;
		}
		
		for (String specifier : getArticles(catalogArticle).keySet()) {
			if (StringUtils.equals(value, specifier)) {
				return true;
			}
		}
		return false;
	}

	public void removeArticle(InventoryArticle iArticle) {
		if (StringUtils.isNotEmpty(iArticle.getSpecifier())) {
			removeArticle(iArticle.getArticle(), iArticle.getSpecifier());
		} else {
			removeArticle(iArticle.getArticle());
		}
	}

	public Map<String, Map<String, InventoryArticle>> getMulitArticles() {
		return mulitArticles;
	}

	public static Inventory fromCart(Person user, Person recipient, Cart cart) throws InvalidPrincipalException, InstantiateDynamicObjectException {
		Inventory inventory = new Inventory(user, recipient);
		inventory.setCartPath(cart.getPath());
		for (CartEntry entry : cart.getCartEntries()) {
			inventory.addCartEntry(entry);
		}
		return inventory;
	}

	private void addCartEntry(CartEntry entry) throws InvalidPrincipalException, InstantiateDynamicObjectException {
		InventoryArticle iArticle = getInventoryArticleFrom(entry);
		if (iArticle!= null) {
			if (entry.getAction() == ACTION.ADD && !hasArticle(iArticle.getArticle()) ||
				(entry.getAction() == ACTION.DELETE || entry.getAction() == ACTION.MODIFY) &&
					hasArticle(iArticle.getArticle())) {
				addArticle(iArticle);
			}
		}
	}

	private InventoryArticle getInventoryArticleFrom(CartEntry entry) throws InvalidPrincipalException, InstantiateDynamicObjectException {
		List<CatalogArticle> availableArticles = Catalog.getCatalog().getAllArticles();
		for (CatalogArticle catalogArticle : availableArticles) {
			if (StringUtils.equals(entry.getId(), catalogArticle.getCatalogPath())) {
				InventoryArticle iArticle = new InventoryArticle(catalogArticle);
				iArticle.setSpecifier(entry.getSpecifier());
				iArticle.setStart(entry.getStart());
				iArticle.setEnd(entry.getEnd());
				iArticle.setStatus(InventoryArticle.getStatus(entry.getAction()));
				if (entry.getAttributes() != null) {
					List<InventoryAttribute> iAttributes = catalogArticle.getInventoryAttributes(catalogArticle, entry.getAttributes());
					if (iAttributes != null) {
						for (InventoryAttribute inventoryAttribute : iAttributes) {
							iArticle.setValue(inventoryAttribute.getAttribute(), inventoryAttribute.getValue());
						}
					}
				}
				return iArticle;
			}
		}
		return null;
	}

	public String getCartPath() {
		return cartPath;
	}

	public void setCartPath(String cartPath) {
		this.cartPath = cartPath;
	}

}