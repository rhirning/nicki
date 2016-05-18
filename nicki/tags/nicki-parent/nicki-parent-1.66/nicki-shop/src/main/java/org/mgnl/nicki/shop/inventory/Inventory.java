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
public class Inventory implements Serializable{
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

	private Map<String, InventoryArticle> articles = new HashMap<String, InventoryArticle>();

	public Map<String, InventoryArticle> getArticles() {
		return articles;
	}

	public Inventory(Person user, Person person) throws InvalidPrincipalException, InstantiateDynamicObjectException {
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

	private void init() throws InvalidPrincipalException, InstantiateDynamicObjectException {
		List<CatalogArticle> availableArticles = CatalogArticleFactory.getInstance().getArticles();
		for (CatalogArticle catalogArticle : availableArticles) {
			List<InventoryAttribute> attributes = getAttributes(catalogArticle);
			Date start = catalogArticle.getStart(person);
			Date end = catalogArticle.getEnd(person);
			String specifier = catalogArticle.getSpecifier(person);
			articles.put(catalogArticle.getPath(), new InventoryArticle(catalogArticle, specifier, start, end, attributes));
		}
	}

	private List<InventoryAttribute> getAttributes(CatalogArticle article) {
		List<InventoryAttribute> attributes = new ArrayList<InventoryAttribute>();
		for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
			CatalogArticleAttribute attribute = iterator.next();
			String value = getCatalogAttributeValue(article, attribute);
			attributes.add(new InventoryAttribute(article, attribute, value));
		}
		return attributes;
	}

	private String getCatalogAttributeValue(CatalogArticle article, CatalogArticleAttribute attribute) {
		String attributeId = article.getAttributeId(attribute.getName());
		for (Iterator<String> iterator = this.attributeValues.iterator(); iterator.hasNext();) {
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

	public InventoryArticle getArticle(CatalogArticle article) {
		return articles.get(article.getPath());
	}

	public void addArticle(CatalogArticle article) {
		InventoryArticle iArticle = getArticle(article);
		if (iArticle == null) {
			articles.put(article.getPath(), new InventoryArticle(article));
		} else if (iArticle.getStatus() == STATUS.DELETED) {
			iArticle.reset();
		}
	}

	public void removeArticle(CatalogArticle article) {
		InventoryArticle iArticle = getArticle(article);
		if (iArticle != null) {
			if (iArticle.getStatus() == STATUS.NEW) {
				articles.remove(article.getPath());
			} else {
				iArticle.setStatus(STATUS.DELETED);
			}
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Inventory for ").append(person.getDisplayName()).append("\n");
		for (Iterator<String> iterator = articles.keySet().iterator(); iterator.hasNext();) {
			String key= iterator.next();
			sb.append(articles.get(key).toString()).append("\n");
		}
		return sb.toString();
	}
	
	public Cart save(String source) throws InstantiateDynamicObjectException, DynamicObjectException {
		if (hasChanged()) {
			Cart cart = person.getContext().getObjectFactory().getDynamicObject(Cart.class);
			cart.initNew(Config.getProperty("nicki.carts.basedn"), Long.toString(new Date().getTime()));
			cart.setInitiator(user);
			cart.setRecipient(person);
			cart.setRequestDate(new Date());
			cart.setStatus(Cart.STATUS.REQUESTED);
			cart.setSource(source);
			cart.setCatalog(Catalog.getCatalog());
			for (Iterator<InventoryArticle> iterator = articles.values().iterator(); iterator.hasNext();) {
				InventoryArticle iArticle = iterator.next();
				if (iArticle.hasChanged()) {
					CartEntry entry = new CartEntry(iArticle.getArticle().getCatalogPath(),
											InventoryArticle.getAction(iArticle.getStatus()));
					for (Iterator<InventoryAttribute> iterator2 = iArticle.getAttributes().values().iterator();
							iterator2.hasNext();) {
						InventoryAttribute iAttribute = iterator2.next();
						if (iAttribute.hasChanged()) {
							entry.addAttribute(iAttribute.getName(), iAttribute.getValue());
						}
					}
					cart.addCartEntry(entry);
				}
			}
			cart.create();
			return cart;
		}
		return null;
	}

	public boolean hasChanged() {
		for (Iterator<InventoryArticle> iterator = articles.values().iterator(); iterator.hasNext();) {
			if (iterator.next().hasChanged()) {
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

}