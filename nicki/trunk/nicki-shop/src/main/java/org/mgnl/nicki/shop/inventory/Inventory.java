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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.shop.Cart;
import org.mgnl.nicki.dynamic.objects.shop.CartEntry;
import org.mgnl.nicki.dynamic.objects.shop.Catalog;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticleAttribute;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticleFactory;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.shop.inventory.InventoryArticle.STATUS;

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
	private List<String> attributeValues;

	private Map<String, Map<SpecifiedArticle, InventoryArticle>> articles = new HashMap<String, Map<SpecifiedArticle, InventoryArticle>>();
	
	public Map<String, Map<SpecifiedArticle, InventoryArticle>> getArticles() {
		return articles;
	}

	public Inventory(Person user, Person person)
			throws InvalidPrincipalException, InstantiateDynamicObjectException {
		super();
		this.setUser(user);
		this.person = person;
		this.attributeValues = getCatalogAttributeValues();
		init();
	}

	private List<String> getCatalogAttributeValues() {
		// TODO Auto-generated method stub
		return null;
	}

	private void init() throws InvalidPrincipalException,
			InstantiateDynamicObjectException {
		List<CatalogArticle> availableArticles = CatalogArticleFactory
				.getInstance().getArticles();
		for (CatalogArticle catalogArticle : availableArticles) {
			List<InventoryAttribute> attributes = getAttributes(catalogArticle);
			String specifier = catalogArticle.getSpecifier(getPerson());
			Date start = catalogArticle.getStart(getPerson(), specifier);
			Date end = catalogArticle.getEnd(getPerson(), specifier);
			SpecifiedArticle specifiedArticle = new SpecifiedArticle(catalogArticle, specifier);
			addArticle(specifiedArticle, new InventoryArticle(
					specifiedArticle, specifier, start, end, attributes));
		}
	}

	private void addArticle(SpecifiedArticle specifiedArticle, InventoryArticle inventoryArticle) {
		CatalogArticle catalogArticle = specifiedArticle.getCatalogArticle();
		Map<SpecifiedArticle, InventoryArticle> map = this.articles.get(catalogArticle.getPath());
		if (map == null) {
			map = new HashMap<SpecifiedArticle, InventoryArticle>();
			this.articles.put(catalogArticle.getPath(), map);
		}
		map.put(specifiedArticle, inventoryArticle);
	}

	private List<InventoryAttribute> getAttributes(CatalogArticle article) {
		List<InventoryAttribute> attributes = new ArrayList<InventoryAttribute>();
		for (Iterator<CatalogArticleAttribute> iterator = article
				.getAllAttributes().iterator(); iterator.hasNext();) {
			CatalogArticleAttribute attribute = iterator.next();
			String value = getCatalogAttributeValue(article, attribute);
			attributes.add(new InventoryAttribute(article, attribute, value));
		}
		return attributes;
	}

	private String getCatalogAttributeValue(CatalogArticle article,
			CatalogArticleAttribute attribute) {
		String attributeId = article.getAttributeId(attribute.getName());
		for (Iterator<String> iterator = this.attributeValues.iterator(); iterator
				.hasNext();) {
			String entry = iterator.next();
			if (StringUtils.startsWith(entry, attributeId + "=")) {
				return StringUtils.substringAfter(entry, attributeId + "=");
			}

		}
		return null;
	}

	public boolean hasArticle(CatalogArticle article) {
		return articles.containsKey(article.getPath());
	}

	public Map<SpecifiedArticle, InventoryArticle> getArticles(CatalogArticle article) {
		return articles.get(article.getPath());
	}

	public void addArticle(SpecifiedArticle specifiedArticle) {
		InventoryArticle iArticle = getInventoryArticle(specifiedArticle);
		if (iArticle == null) {
			this.addArticle(specifiedArticle, new InventoryArticle(specifiedArticle));
		} else if (iArticle.getStatus() == STATUS.DELETED) {
			iArticle.reset();
		}
	}
	
	public InventoryArticle getInventoryArticle(SpecifiedArticle specifiedArticle) {
		CatalogArticle catalogArticle = specifiedArticle.getCatalogArticle();
		Map<SpecifiedArticle, InventoryArticle> map = this.articles.get(catalogArticle.getPath());
		if (map != null) {
			return map.get(specifiedArticle);
		}
		return null;
	}

	public void removeArticle(SpecifiedArticle specifiedArticle) {
		CatalogArticle catalogArticle = specifiedArticle.getCatalogArticle();
		InventoryArticle iArticle = getInventoryArticle(specifiedArticle);
		if (iArticle != null) {
			if (iArticle.getStatus() == STATUS.NEW) {
				removeArticle(catalogArticle.getPath(), specifiedArticle);
			} else {
				iArticle.setStatus(STATUS.DELETED);
			}
		}
	}

	private void removeArticle(String path, SpecifiedArticle specifiedArticle) {
		Map<SpecifiedArticle, InventoryArticle> map = this.articles.get(path);
		if (map != null) {
			if (map.containsKey(specifiedArticle));
			map.remove(specifiedArticle);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Inventory for ").append(person.getDisplayName())
				.append("\n");
		for (Iterator<String> iterator = articles.keySet().iterator(); iterator
				.hasNext();) {
			String key = iterator.next();
			sb.append(articles.get(key).toString()).append("\n");
		}
		return sb.toString();
	}

	public Cart getCart(String source) {
		if (hasChanged()) {
			Cart cart;
			try {
				cart = person.getContext().getObjectFactory()
						.getDynamicObject(Cart.class);
				cart.initNew(Config.getProperty("nicki.carts.basedn"),
						Long.toString(new Date().getTime()));
				cart.setInitiator(user);
				cart.setRecipient(person);
				cart.setRequestDate(new Date());
				cart.setStatus(Cart.STATUS.REQUESTED);
				cart.setSource(source);
				cart.setCatalog(Catalog.getCatalog());
				for (String key : articles.keySet()) {
					Map<SpecifiedArticle, InventoryArticle> list = articles.get(key);
					for (SpecifiedArticle specifiedArticle : list.keySet()) {
						InventoryArticle iArticle = list.get(specifiedArticle);
						if (iArticle.hasChanged()) {
							CartEntry entry = new CartEntry(
									iArticle.getArticle().getCatalogPath(),
									InventoryArticle.getAction(iArticle.getStatus()));
							for (Iterator<InventoryAttribute> iterator2 = iArticle
									.getAttributes().values().iterator(); iterator2
									.hasNext();) {
								InventoryAttribute iAttribute = iterator2.next();
								if (iAttribute.hasChanged()) {
									entry.addAttribute(iAttribute.getName(),
											iAttribute.getValue());
								}
							}
							cart.addCartEntry(entry);
						}
					}
				}
				return cart;
			} catch (InstantiateDynamicObjectException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Cart save(String source) throws InstantiateDynamicObjectException,
			DynamicObjectException {
		if (hasChanged()) {
			Cart cart = getCart(source);
			cart.create();
			return cart;
		}
		return null;
	}

	public boolean hasChanged() {
		for (String key : articles.keySet()) {
			Map<SpecifiedArticle, InventoryArticle> map = articles.get(key);
			for (SpecifiedArticle specifiedArticle : map.keySet()) {
				InventoryArticle iArticle = map.get(specifiedArticle);
				if (iArticle.hasChanged()) {
					return true;
				}
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

	public SpecifiedArticle getFirstSpecifiedArticle(CatalogArticle article) {
		Map<SpecifiedArticle, InventoryArticle> map = this.articles.get(article.getPath());
		if (map != null) {
			for (SpecifiedArticle specifiedArticle : map.keySet()) {
				return specifiedArticle;
			}
		}
		return null;
	}

	public InventoryArticle getFirstInventoryArticle(CatalogArticle article) {
		Map<SpecifiedArticle, InventoryArticle> map = this.articles.get(article.getPath());
		if (map != null) {
			for (SpecifiedArticle specifiedArticle : map.keySet()) {
				return map.get(specifiedArticle);
			}
		}
		return null;
	}

}
