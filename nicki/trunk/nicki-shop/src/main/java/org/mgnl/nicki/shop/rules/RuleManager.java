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
package org.mgnl.nicki.shop.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.shop.inventory.Inventory;
import org.mgnl.nicki.shop.objects.Cart;
import org.mgnl.nicki.shop.objects.CartEntry;
import org.mgnl.nicki.shop.objects.Catalog;
import org.mgnl.nicki.shop.objects.CatalogArticle;
import org.mgnl.nicki.shop.objects.Selector;
import org.mgnl.nicki.shop.objects.ValueProvider;

/* 
 * Attribute nickiDirectory: Pfad getrennt mit "/"
 */
public class RuleManager {
	private static String activeFilter;
	
	static {
		try {
			activeFilter = AppContext.getSystemContext().getObjectFactory().getDynamicObject(Person.class).getActiveFilter();
		} catch (Exception e) {
			activeFilter = null;
			e.printStackTrace();
		}

	}
	
	public static List<CatalogArticle> getArticles(Person person) {
		String query = getArticleQuery(person);
		return person.getContext().loadObjects(CatalogArticle.class,
				Config.getProperty("nicki.catalogs.basedn"), query);
	}
	
	public static String getArticleQuery(Person person) {
		StringBuffer sb = new StringBuffer();
		LdapHelper.addQuery(sb, "nickiRule=*", LOGIC.AND);
		List<Selector> selectors = person.getContext().loadChildObjects(Selector.class, 
				Config.getProperty("nicki.selectors.basedn"), ""); 
		for (Selector selector : selectors) {
			Object value = person.get(selector.getName());
			if (selector.hasValueProvider()) {
				ValueProvider valueProvider = selector.getValueProvider();
				LdapHelper.addQuery(sb, valueProvider.getArticleQuery(person, value), LOGIC.AND);
			} else {
				StringBuffer sb2 = new StringBuffer();
				LdapHelper.addQuery(sb2, "nickiRule=" + selector.getName() + "=*", LOGIC.OR);
				LdapHelper.negateQuery(sb2);
				if (value == null) {
					// nothing to add
				} else if (value instanceof String) {
					String stringValue = (String) value;
					LdapHelper.addQuery(sb2, "nickiRule=" + selector.getName() + "=" + stringValue, LOGIC.OR);
				} else if (value instanceof List) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>) value;
					for (String stringValue : values) {
						LdapHelper.addQuery(sb2, "nickiRule=" + selector.getName() + "=" + stringValue, LOGIC.OR);
					}
				}
				LdapHelper.addQuery(sb, sb2.toString(), LOGIC.AND);
			}
		}
		return sb.toString();
	}

	public static List<Person> getUsers(CatalogArticle article, String additionalQuery) {
		if (!article.hasRules()) {
			if (StringUtils.isEmpty(additionalQuery)) {
				return new ArrayList<Person>();
			} else {
				
			}
		}
		
		RuleQuery query = getRuleQuery(article);
		if (!query.isNeedQuery()) {
			return new ArrayList<Person>();
		}
		StringBuffer filter = new StringBuffer();
		if (StringUtils.isNotEmpty(query.getQuery())) {
			LdapHelper.addQuery(filter, query.getQuery(), LOGIC.AND);
		}
		if (StringUtils.isNotEmpty(additionalQuery)) {
			LdapHelper.addQuery(filter, additionalQuery, LOGIC.AND);
		}
		return article.getContext().loadObjects(Person.class, query.getBaseDn(), filter.toString());
	}

	// TODO
	public static String getAssignedRuleArticlesQuery(Person person) {
		StringBuffer sb = new StringBuffer();
		@SuppressWarnings("unchecked")
		List<String> articles = (List<String>) person.get(Person.ATTRIBUTE_ASSIGNEDARTICLE);
		if (articles != null && articles.size() > 0) {
			for (String article : articles) {
				LdapHelper.addQuery(sb, "entryDn=" + article, LOGIC.OR);
			}
		}
		return sb.toString();
	}
	
	public static String getAssignedRulePersonsQuery(CatalogArticle article) {
		return "(nickiCatalogArticle=rule#" + article.getPath() + "#*)";
	}
	
	// missing = plan - assigned 	= (plannedArticles) && !(assignedArticles)
	// surplus = assigned - planned = (assignedArticles) && !(plannedArticles)
	public static ChangeSet getChangeSet(Person person) {
		ChangeSet changeSet = new ChangeSet();
		String planned = getArticleQuery(person);
		String assigned = getAssignedRuleArticlesQuery(person);
		
		// missing
		StringBuffer sb = new StringBuffer();
		LdapHelper.addQuery(sb, assigned, LOGIC.AND);
		LdapHelper.negateQuery(sb);
		LdapHelper.addQuery(sb, planned, LOGIC.AND);
		List<CatalogArticle> missingArticles = person.getContext().loadObjects(CatalogArticle.class,
				Config.getProperty("nicki.catalogs.basedn"), sb.toString());
		if (missingArticles != null && missingArticles.size() > 0) {
			for (CatalogArticle article : missingArticles) {
				changeSet.addToMissing(person, article);
			}
		}
		
		// surplus
		sb.setLength(0);
		LdapHelper.addQuery(sb, planned, LOGIC.AND);
		LdapHelper.negateQuery(sb);
		LdapHelper.addQuery(sb, assigned, LOGIC.AND);
		List<CatalogArticle> surplusArticles = person.getContext().loadObjects(CatalogArticle.class,
				Config.getProperty("nicki.catalogs.basedn"), sb.toString());
		if (surplusArticles != null && surplusArticles.size() > 0) {
			for (CatalogArticle article : surplusArticles) {
				changeSet.addToSurplus(person, article);
			}
		}
		return changeSet;
	}
	
	// missing = plan - assigned 	= (plannedArticles) && !(assignedArticles)
	// surplus = assigned - planned = (assignedArticles) && !(plannedArticles)
	public static ChangeSet getChangeSet(CatalogArticle article) {
		ChangeSet changeSet = new ChangeSet();
		StringBuffer sb = new StringBuffer();
		// missing
		if (article.hasRules()) {
			LdapHelper.addQuery(sb, getAssignedRulePersonsQuery(article), LOGIC.AND);
			LdapHelper.negateQuery(sb);
			Collection<Person> missing = getUsers(article, sb.toString());
			for (Person person : missing) {
				changeSet.addToMissing(person, article);
			}
		}
		
		return changeSet;
	}
	
	

	public static RuleQuery getRuleQuery(CatalogArticle article) {
		RuleQuery ruleQuery = new RuleQuery();
		if (!article.hasRules()) {
			return ruleQuery;
		}
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (String entry : article.getRules()) {
			add(map,entry);
		}
		StringBuffer sb = new StringBuffer();
		for (String selectorName : map.keySet()) {
			StringBuffer sb2 = new StringBuffer();
			for (String value : map.get(selectorName)) {
				Selector selector = getSelector(article, selectorName);
				if (selector.hasValueProvider()) {
					ValueProvider valueProvider = selector.getValueProvider();
					String query = valueProvider.getPersonQuery(article, value);
					LdapHelper.addQuery(sb2, query, LOGIC.OR);
				} else {
					String query = getPersonQuery(article, selectorName, value);
					LdapHelper.addQuery(sb2, query, LOGIC.OR);
				}
			}
			if (sb2.length() > 0) {
				LdapHelper.addQuery(sb, sb2.toString(), LOGIC.AND);
			}
		}
		if (activeFilter != null) {
			LdapHelper.addQuery(sb, activeFilter, LOGIC.AND);
		}

		ruleQuery.setBaseDn(Config.getProperty("nicki.users.basedn"));
		ruleQuery.setQuery(sb.toString());
		return ruleQuery;
	}

	private static Selector getSelector(CatalogArticle article, String selectorName) {
		return article.getContext().loadObject(Selector.class, "cn=" + selectorName + "," + Config.getProperty("nicki.selectors.basedn"));
	}

	public static String getPersonQuery(CatalogArticle article, String selectorName, String value) {
		Selector selector = getSelector(article, selectorName);
		if (selector.hasValueProvider()) {
			return selector.getValueProvider().getPersonQuery(article, value);
		} else {
			return BasicValueProvider.getLdapName(article, selectorName) + "=" + value;
		}
	}

	private static void add(Map<String, List<String>> map, String entry) {
		String parts[] = StringUtils.split(entry, "=");
		if (parts.length == 2) {
			List<String> list = map.get(parts[0]);
			if (list == null) {
				list = new ArrayList<String>();
				map.put(parts[0], list);
			}
			list.add(parts[1]);
		}
	}
	
	public Cart saveChangeCart(Person person, Person user) throws InstantiateDynamicObjectException, DynamicObjectException {
		ChangeSet changeSet = getChangeSet(person);
		if (changeSet != null && changeSet.isNotEmpty()) {
			Cart cart = user.getContext().getObjectFactory().getDynamicObject(Cart.class);
			cart.initNew(Config.getProperty("nicki.carts.basedn"), Long.toString(new Date().getTime()));
			cart.setInitiator(user);
			cart.setRecipient(person);
			cart.setRequestDate(new Date());
			cart.setCartStatus(Cart.CART_STATUS.REQUESTED);
			cart.setSource(Inventory.SOURCE.RULE.getValue());
			cart.setCatalog(Catalog.getCatalog());
			for (ArticleChange article : changeSet.getChanges()) {
				CartEntry entry = new CartEntry(article.getArticle().getCatalogPath(), article.getCartEntryType());
				cart.addCartEntry(entry);
			}
			cart.create();
			return cart;
		}
		return null;
	}

	public void saveChangeCarts(CatalogArticle catalogArticle, Person user) throws InstantiateDynamicObjectException, DynamicObjectException {
		ChangeSet changeSet = getChangeSet(catalogArticle);
		if (changeSet != null && changeSet.isNotEmpty()) {
			for (ArticleChange article : changeSet.getChanges()) {
				Cart cart = user.getContext().getObjectFactory().getDynamicObject(Cart.class);
				cart.initNew(Config.getProperty("nicki.carts.basedn"), Long.toString(new Date().getTime()));
				cart.setInitiator(user);
				cart.setRecipient(article.getPerson());
				cart.setRequestDate(new Date());
				cart.setCartStatus(Cart.CART_STATUS.REQUESTED);
				cart.setSource(Inventory.SOURCE.RULE.getValue());
				cart.setCatalog(Catalog.getCatalog());
	
				CartEntry entry = new CartEntry(article.getArticle().getCatalogPath(), article.getCartEntryType());
				cart.addCartEntry(entry);

				cart.create();
			}
		}
	}


}
