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
package org.mgnl.nicki.shop.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.methods.LoadObjectsMethod;
import org.mgnl.nicki.ldap.objects.BaseLdapDynamicObject;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObjectException;


@SuppressWarnings("serial")
public class Catalog extends BaseLdapDynamicObject {
	public static final String PATH_SEPARATOR = "/";
	private static long lastBuild = 0;
	private static long buildInterval = 10*60*1000; // ms
	private static Catalog instance = null;
	
	private List<CatalogPage> pages = null;
	private List<CatalogArticle> articles = null;

	public Catalog() {
		
	}

	public void initDataModel() {
		// objectClass
		addObjectClass("nickiCatalog");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		addMethod("allArticles", new LoadObjectsMethod(CatalogArticle.class, this, "objectClass=nickiCatalogArticle"));

		addChild("page", CatalogPage.class);
	}

	/*
	 * CatalogID: /separatedPathToArticle
	 */
	public CatalogArticle getArticle(String catalogArticleId) {
		String key = catalogArticleId;
		if (StringUtils.startsWith(key, PATH_SEPARATOR)) {
			key = StringUtils.substring(catalogArticleId, 1);
		}
		String pageKey = StringUtils.substringBefore(key, PATH_SEPARATOR);
		String articleKey = StringUtils.substringAfter(key, PATH_SEPARATOR);
		CatalogPage page = getPage(pageKey);
		if (page != null) {
			return page.getArticle(articleKey);
		} else {
			return null;
		}
	}
	
	public CatalogPage getPage(String key) {
		return getContext().loadChildObject(CatalogPage.class, this, key);
	}

	private List<CatalogArticle> getArticles() {
		if (articles == null) {
			articles = new ArrayList<CatalogArticle>();
			for (Iterator<CatalogPage> iterator = getPages().iterator(); iterator
					.hasNext();) {
				CatalogPage page= iterator.next();
				articles.addAll(page.getAllArticles()); 
			}
		}
		return articles;
	}
	
	public static synchronized Catalog getCatalog() {
		if (lastBuild + buildInterval < new Date().getTime()) {
			instance = null;
		}
		if (instance == null) {
			load();
			lastBuild = new Date().getTime();
		}
		return instance;
	}

	private static void load() {
		try {
			instance = AppContext.getSystemContext().loadObject(Catalog.class, Config.getProperty("nicki.catalog"));
		} catch (InvalidPrincipalException e) {
			e.printStackTrace();
			instance = null;
		}
	}

	public CatalogPage getReferencedPage(
			String referencedPage) {
		String key = referencedPage;
		if (StringUtils.startsWith(key, PATH_SEPARATOR)) {
			key = StringUtils.substring(referencedPage, 1);
		}

		if (StringUtils.contains(key, PATH_SEPARATOR)) {
			String pageKey = StringUtils.substringBefore(key, PATH_SEPARATOR);
			String subPageKey = StringUtils.substringAfter(key, PATH_SEPARATOR);
			return getPage(pageKey).getPage(subPageKey);
		} else {
			return getPage(key);
		}
	}

	public List<CatalogArticle> getArticlesForPath(String path) {
		List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
		for (Iterator<CatalogArticle> iterator = getAllArticles().iterator(); iterator.hasNext();) {
			CatalogArticle article = iterator.next();
			if (StringUtils.equalsIgnoreCase(article.getArticlePath(), path)) {
				catalogArticles.add(article);
			}
		}
		return catalogArticles;
	}

	public List<CatalogPage> getPages() {
		if (pages == null) {
			pages = getContext().loadChildObjects(CatalogPage.class, this.getPath(), null);
			for (CatalogPage page : pages) {
				try {
					getContext().loadObject(page);
				} catch (DynamicObjectException e) {
					e.printStackTrace();
				}
			}
		}
		return pages;
	}
	
	public List<CatalogArticle> getAllArticles(Person person) {
		List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
		for (Iterator<CatalogArticle> iterator = getAllArticles().iterator(); iterator.hasNext();) {
			CatalogArticle catalogArticle = iterator.next();
			if (hasArticle(person, catalogArticle)) {
				catalogArticles.add(catalogArticle);
			}
		}
		return catalogArticles;
	}

	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
		for (Iterator<CatalogArticle> iterator = getArticles().iterator(); iterator.hasNext();) {
			CatalogArticle catalogArticle = iterator.next();
			catalogArticles.add(catalogArticle);
		}
		return catalogArticles;
	}


	public boolean hasArticle(Person person, CatalogArticle article) {
		return article.hasArticle(person, article);
	}

	public List<CatalogArticle> getArticles(String path) {
		String key = path;
		if (StringUtils.startsWith(key, PATH_SEPARATOR)) {
			key = StringUtils.substring(path, 1);
		}
		String pageKey = StringUtils.substringBefore(key, PATH_SEPARATOR);
		String articleKey = StringUtils.substringAfter(key, PATH_SEPARATOR);
		CatalogPage page = getPage(pageKey);
		if (page != null) {
			return page.getArticles(articleKey);
		} else {
			return null;
		}
	}

	public List<CatalogArticle> getArticlesOfType(CatalogArticle catalogArticle) {
		List<CatalogArticle> result = new ArrayList<CatalogArticle>();
		for (CatalogArticle article : getAllArticles()) {
			if (catalogArticle.getClass().isAssignableFrom(article.getClass())) {
				result.add(article);
			}
		}
		return result;
	}

}
