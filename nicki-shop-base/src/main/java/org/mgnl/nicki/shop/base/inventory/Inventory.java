
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

import org.apache.commons.lang3.StringUtils;
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

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class Inventory.
 */
@Slf4j
@SuppressWarnings("serial")
public class Inventory implements Serializable {
	
	/**
	 * The Enum SOURCE.
	 */
	public enum SOURCE {
		
		/** The shop. */
		SHOP, 
 /** The rule. */
 RULE, 
 /** The none. */
 NONE;

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return this.toString().toLowerCase();
		}

		/**
		 * From string.
		 *
		 * @param str the str
		 * @return the source
		 */
		public static SOURCE fromString(String str) {
			try {
				return SOURCE.valueOf(str.toUpperCase());
			} catch (Exception e) {
				return NONE;
			}
		}
	};

	/** The user. */
	private Person user;
	
	/** The person. */
	private Person person;
	
	/** The cart path. */
	private String cartPath;

	/** The articles. */
	private Map<String, InventoryArticle> articles = new HashMap<String, InventoryArticle>();
	
	/** The multi articles. */
	private Map<String, Map<String, InventoryArticle>> multiArticles = new HashMap<String, Map<String, InventoryArticle>>();
	
	/**
	 * Gets the articles.
	 *
	 * @return the articles
	 */
	public Map<String, InventoryArticle> getArticles() {
		return articles;
	}

	/**
	 * Instantiates a new inventory.
	 *
	 * @param user the user
	 * @param person the person
	 * @throws InvalidPrincipalException the invalid principal exception
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public Inventory(Person user, Person person)
			throws InvalidPrincipalException, InstantiateDynamicObjectException {
		super();
		this.setUser(user);
		this.person = person;
		init();
	}

	/**
	 * Inits the.
	 *
	 * @throws InvalidPrincipalException the invalid principal exception
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	private void init() throws InvalidPrincipalException, InstantiateDynamicObjectException {
		for (CatalogArticle catalogArticle : Catalog.getCatalog().getAllArticles()) {
			for (InventoryArticle inventoryArticle : catalogArticle.getInventoryArticles(this.person)) {
				addInventoryArticle(inventoryArticle);
			}
		}
	}
	

	/**
	 * Adds the article.
	 *
	 * @param catalogArticle the catalog article
	 * @return the inventory article
	 */
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
	
	/**
	 * Adds the article.
	 *
	 * @param inventoryArticle the inventory article
	 */
	public void addArticle(InventoryArticle inventoryArticle) {
		addInventoryArticle(inventoryArticle);
	}
	
	/**
	 * Adds the inventory article.
	 *
	 * @param inventoryArticle the inventory article
	 */
	private void addInventoryArticle(InventoryArticle inventoryArticle) {
		if (inventoryArticle.getArticle().isMultiple()) {
			addArticle(inventoryArticle.getArticle(), inventoryArticle.getSpecifier(), inventoryArticle);
		} else {
			addArticle(inventoryArticle.getArticle(), inventoryArticle);
		}
	}

	
	/**
	 * Adds the article.
	 *
	 * @param catalogArticle the catalog article
	 * @param specifier the specifier
	 * @param inventoryArticle the inventory article
	 */
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

	/**
	 * Adds the article.
	 *
	 * @param catalogArticle the catalog article
	 * @param inventoryArticle the inventory article
	 */
	private void addArticle(CatalogArticle catalogArticle, InventoryArticle inventoryArticle) {
		if (!this.articles.containsKey(catalogArticle.getPath())) {
			this.articles.put(catalogArticle.getPath(), inventoryArticle);
		} else {
			InventoryArticle iArticle = this.articles.get(catalogArticle.getPath());
			iArticle.setStatus(iArticle.getOriginalStatus());
		}
	}

	/**
	 * Checks for article.
	 *
	 * @param article the article
	 * @return true, if successful
	 */
	public boolean hasArticle(CatalogArticle article) {
		return articles.containsKey(article.getPath()) || multiArticles.containsKey(article.getPath());
	}

	/**
	 * Gets the article.
	 *
	 * @param article the article
	 * @return the article
	 */
	public InventoryArticle getArticle(CatalogArticle article) {
		return articles.get(article.getPath());
	}

	/**
	 * Gets the articles.
	 *
	 * @param article the article
	 * @return the articles
	 */
	public Map<String, InventoryArticle> getArticles(CatalogArticle article) {
		return multiArticles.get(article.getPath());
	}

	/**
	 * Adds the article.
	 *
	 * @param catalogArticle the catalog article
	 * @param specifier the specifier
	 */
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
	
	/**
	 * Gets the inventory article.
	 *
	 * @param catalogArticle the catalog article
	 * @return the inventory article
	 */
	public InventoryArticle getInventoryArticle(CatalogArticle catalogArticle) {
		return this.articles.get(catalogArticle.getPath());
	}
	
	/**
	 * Gets the inventory article.
	 *
	 * @param catalogArticle the catalog article
	 * @param specifier the specifier
	 * @return the inventory article
	 */
	public InventoryArticle getInventoryArticle(CatalogArticle catalogArticle, String specifier) {
		Map<String, InventoryArticle> map = this.multiArticles.get(catalogArticle.getPath());
		if (map != null) {
			return map.get(specifier);
		}
		return null;
	}

	/**
	 * Removes the article.
	 *
	 * @param catalogArticle the catalog article
	 * @param specifier the specifier
	 */
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

	/**
	 * Removes the article.
	 *
	 * @param catalogArticle the catalog article
	 */
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

	/**
	 * Removes the article.
	 *
	 * @param path the path
	 * @param specifier the specifier
	 */
	private void removeArticle(String path, String specifier) {
		Map<String, InventoryArticle> map = this.multiArticles.get(path);
		if (map != null && map.containsKey(specifier)) {
			map.remove(specifier);
		}
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Inventory for ").append(person.getDisplayName())
				.append("\n");
		for (String key : articles.keySet()) {
			sb.append(articles.get(key).toString()).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Gets the cart.
	 *
	 * @param source the source
	 * @param cartStatus the cart status
	 * @return the cart
	 */
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
				log.error("Error", e);
			}
		}
		return null;
	}

	/**
	 * Save.
	 *
	 * @param source the source
	 * @param oldCart the old cart
	 * @return the cart
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 * @throws DynamicObjectException the dynamic object exception
	 */
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

	/**
	 * Remember.
	 *
	 * @param source the source
	 * @param oldCart the old cart
	 * @return the cart
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 * @throws DynamicObjectException the dynamic object exception
	 */
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

	/**
	 * Checks for changed.
	 *
	 * @return true, if successful
	 */
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

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	private void setUser(Person user) {
		this.user = user;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public Person getUser() {
		return user;
	}

	/**
	 * Gets the person.
	 *
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * Checks for article.
	 *
	 * @param catalogArticle the catalog article
	 * @param value the value
	 * @return true, if successful
	 */
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

	/**
	 * Removes the article.
	 *
	 * @param iArticle the i article
	 */
	public void removeArticle(InventoryArticle iArticle) {
		if (StringUtils.isNotEmpty(iArticle.getSpecifier())) {
			removeArticle(iArticle.getArticle(), iArticle.getSpecifier());
		} else {
			removeArticle(iArticle.getArticle());
		}
	}

	/**
	 * Gets the multi articles.
	 *
	 * @return the multi articles
	 */
	public Map<String, Map<String, InventoryArticle>> getMultiArticles() {
		return multiArticles;
	}

	/**
	 * From cart.
	 *
	 * @param user the user
	 * @param recipient the recipient
	 * @param cart the cart
	 * @return the inventory
	 * @throws InvalidPrincipalException the invalid principal exception
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public static Inventory fromCart(Person user, Person recipient, Cart cart) throws InvalidPrincipalException, InstantiateDynamicObjectException {
		Inventory inventory = new Inventory(user, recipient);
		inventory.setCartPath(cart.getPath());
		for (CartEntry entry : cart.getCartEntries()) {
			inventory.addCartEntry(entry);
		}
		return inventory;
	}

	/**
	 * Adds the cart entry.
	 *
	 * @param entry the entry
	 * @throws InvalidPrincipalException the invalid principal exception
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
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
		
	/**
	 * Creates the inventory article from.
	 *
	 * @param entry the entry
	 * @return the inventory article
	 * @throws InvalidPrincipalException the invalid principal exception
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
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

	/**
	 * Gets the cart path.
	 *
	 * @return the cart path
	 */
	public String getCartPath() {
		return cartPath;
	}

	/**
	 * Sets the cart path.
	 *
	 * @param cartPath the new cart path
	 */
	public void setCartPath(String cartPath) {
		this.cartPath = cartPath;
	}

	/**
	 * Modify article.
	 *
	 * @param catalogArticle the catalog article
	 * @param oldSpecifier the old specifier
	 * @param newSpecifier the new specifier
	 */
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
