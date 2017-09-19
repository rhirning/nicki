
package org.mgnl.nicki.shop.base.inventory;

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
import org.mgnl.nicki.shop.base.objects.Cart;
import org.mgnl.nicki.shop.base.objects.CartEntry;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CartEntry.ACTION;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle.STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class Inventory implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(Inventory.class);
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
	
	private String cartPath;

	private Map<String, InventoryArticle> articles = new HashMap<String, InventoryArticle>();
	
	private Map<String, Map<String, InventoryArticle>> multiArticles = new HashMap<String, Map<String, InventoryArticle>>();
	
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
		for (CatalogArticle catalogArticle : Catalog.getCatalog().getAllArticles()) {
			for (InventoryArticle inventoryArticle : catalogArticle.getInventoryArticles(this.person)) {
				addInventoryArticle(inventoryArticle);
			}
		}
	}
	

	public InventoryArticle addArticle(CatalogArticle catalogArticle) {
		if (!this.articles.containsKey(catalogArticle.getPath())) {
			InventoryArticle inventoryArticle = new InventoryArticle(catalogArticle);
			this.articles.put(catalogArticle.getPath(), inventoryArticle);
			return inventoryArticle;
		} else {
			InventoryArticle inventoryArticle = this.articles.get(catalogArticle.getPath());
			inventoryArticle.setStatus(inventoryArticle.getOriginalStatus());
			return this.articles.get(catalogArticle.getPath());
		}
	}
	
	public void addArticle(InventoryArticle inventoryArticle) {
		addInventoryArticle(inventoryArticle);
	}
	
	private void addInventoryArticle(InventoryArticle inventoryArticle) {
		if (inventoryArticle.getArticle().isMultiple()) {
			addArticle(inventoryArticle.getArticle(), inventoryArticle.getSpecifier(), inventoryArticle);
		} else {
			addArticle(inventoryArticle.getArticle(), inventoryArticle);
		}
	}

	
	private void addArticle(CatalogArticle catalogArticle, String specifier, InventoryArticle inventoryArticle) {
		Map<String, InventoryArticle> map = this.multiArticles.get(catalogArticle.getPath());
		if (map == null) {
			map = new HashMap<String, InventoryArticle>();
			this.multiArticles.put(catalogArticle.getPath(), map);
		}

		InventoryArticle iArticle = getInventoryArticle(catalogArticle, specifier);
		if (iArticle == null) {
			map.put(specifier, inventoryArticle);
		} else {
			iArticle.setStatus(iArticle.getOriginalStatus());
		}
	}

	private void addArticle(CatalogArticle catalogArticle, InventoryArticle inventoryArticle) {
		if (!this.articles.containsKey(catalogArticle.getPath())) {
			this.articles.put(catalogArticle.getPath(), inventoryArticle);
		} else {
			InventoryArticle iArticle = this.articles.get(catalogArticle.getPath());
			iArticle.setStatus(iArticle.getOriginalStatus());
		}
	}

	public boolean hasArticle(CatalogArticle article) {
		return articles.containsKey(article.getPath()) || multiArticles.containsKey(article.getPath());
	}

	public InventoryArticle getArticle(CatalogArticle article) {
		return articles.get(article.getPath());
	}

	public Map<String, InventoryArticle> getArticles(CatalogArticle article) {
		return multiArticles.get(article.getPath());
	}

	public void addArticle(CatalogArticle catalogArticle, String specifier) {
		InventoryArticle iArticle = getInventoryArticle(catalogArticle, specifier);
		if (iArticle == null) {
			Map<String, InventoryArticle> map = this.multiArticles.get(catalogArticle.getPath());
			if (map == null) {
				map = new HashMap<String, InventoryArticle>();
				this.multiArticles.put(catalogArticle.getPath(), map);
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
		Map<String, InventoryArticle> map = this.multiArticles.get(catalogArticle.getPath());
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
			InventoryArticle iArticle = this.articles.get(catalogArticle.getPath());
			if (iArticle.getStatus() == STATUS.NEW) {
				this.articles.remove(catalogArticle.getPath());
			} else {
				iArticle.setStatus(STATUS.DELETED);
			}
		}
	}

	private void removeArticle(String path, String specifier) {
		Map<String, InventoryArticle> map = this.multiArticles.get(path);
		if (map != null && map.containsKey(specifier)) {
			map.remove(specifier);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
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
				cart.getContext().getAdapter().initNew(cart, Config.getString("nicki.carts.basedn"),
						Long.toString(new Date().getTime()));
				cart.setInitiator(user);
				cart.setRecipient(person);
				cart.setRequestDate(new Date());
				cart.setCartStatus(cartStatus);
				cart.setSource(source);
				cart.setCatalog(Catalog.getCatalog());
				for (String key : multiArticles.keySet()) {
					Map<String, InventoryArticle> list = multiArticles.get(key);
					for (String specifier : list.keySet()) {
						InventoryArticle iArticle = list.get(specifier);
						if (iArticle.hasChanged()) {
							CartEntry entry = new CartEntry(
									iArticle,
									iArticle.getArticle().getCatalogPath(), specifier,
									InventoryArticle.getAction(iArticle.getStatus()));
							entry.setComment(iArticle.getComment());
							entry.setStart(iArticle.getStart());
							entry.setEnd(iArticle.getEnd());
							cart.addCartEntry(entry);
						}
					}
				}
				for (String key : articles.keySet()) {
					InventoryArticle iArticle = articles.get(key);
					if (iArticle.hasChanged()) {
						CartEntry entry = new CartEntry(
								iArticle,
								iArticle.getArticle().getCatalogPath(),
								InventoryArticle.getAction(iArticle.getStatus()));
						entry.setComment(iArticle.getComment());
						cart.addCartEntry(entry);
					}
				}
				return cart;
			} catch (InstantiateDynamicObjectException e) {
				LOG.error("Error", e);
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
			Cart newCart = (Cart) cart.create();
			oldCart = newCart;
			return newCart;
		}
		return null;
	}

	public boolean hasChanged() {
		for (String key : multiArticles.keySet()) {
			Map<String, InventoryArticle> map = multiArticles.get(key);
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

	private void setUser(Person user) {
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

	public Map<String, Map<String, InventoryArticle>> getMultiArticles() {
		return multiArticles;
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
		CatalogArticle catalogArticle = entry.getCatalogArticle();
		if (catalogArticle != null) {
			if (!catalogArticle.isMultiple()) {
				if (entry.getAction() == ACTION.ADD && !hasArticle(catalogArticle)) {
					addArticle(createInventoryArticleFrom(entry));
				} else if (entry.getAction() == ACTION.DELETE &&
							hasArticle(catalogArticle)) {
					InventoryArticle iArticle = getInventoryArticle(catalogArticle);
					iArticle.setStatus(STATUS.DELETED);
				}
			} else {
				if (entry.getAction() == ACTION.ADD && !hasArticle(catalogArticle, entry.getSpecifier())) {
					addArticle(createInventoryArticleFrom(entry));
				} else if (entry.getAction() == ACTION.DELETE &&
							hasArticle(catalogArticle, entry.getSpecifier())) {
					InventoryArticle iArticle = getInventoryArticle(catalogArticle, entry.getSpecifier());
					iArticle.setStatus(STATUS.DELETED);
				}
			}
		}
	}
		
	private InventoryArticle createInventoryArticleFrom(CartEntry entry) throws InvalidPrincipalException, InstantiateDynamicObjectException {
		
		List<CatalogArticle> availableArticles = Catalog.getCatalog().getAllArticles();
		for (CatalogArticle catalogArticle : availableArticles) {
			if (StringUtils.equals(entry.getId(), catalogArticle.getCatalogPath())) {
				InventoryArticle iArticle = new InventoryArticle(catalogArticle);
				iArticle.setSpecifier(entry.getSpecifier());
				iArticle.setStart(entry.getStart());
				iArticle.setEnd(entry.getEnd());
				iArticle.setStatus(InventoryArticle.getStatus(entry.getAction()));
				iArticle.setComment(entry.getComment());
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

	public void modifyArticle(CatalogArticle catalogArticle, 
			String oldSpecifier, String newSpecifier) {
		InventoryArticle iArticle = getInventoryArticle(catalogArticle, oldSpecifier);
		if (iArticle != null
			&& iArticle.getStatus() == STATUS.NEW) {
			removeArticle(catalogArticle.getPath(), oldSpecifier);
			addArticle(catalogArticle, newSpecifier);
		}
	}

}
