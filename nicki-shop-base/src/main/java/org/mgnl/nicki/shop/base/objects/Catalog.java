
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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.Child;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.core.objects.DynamicObjectException;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class Catalog.
 */
@Slf4j
@DynamicObject
@ObjectClass("nickiCatalog")
@Child(name="child", objectFilter={CatalogPage.class})
public class Catalog extends CatalogObject{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1114608130611536361L;
	
	/** The Constant PATH_SEPARATOR. */
	public static final String PATH_SEPARATOR = "/";
	
	/** The last build. */
	private static long lastBuild = 0;
	
	/** The build interval. */
	private static long buildInterval = 10*60*1000; // ms
	
	/** The instance. */
	private static Catalog instance;
	
	/** The pages. */
	private List<CatalogPage> pages;
	
	/** The articles. */
	private List<CatalogArticle> articles;

	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/** The category. */
	@DynamicAttribute(externalName="nickiCategory")
	private String[] category;
	
	/**
	 * Instantiates a new catalog.
	 */
	public Catalog() {
		
	}

	/**
	 * Gets the article.
	 *
	 * @param catalogArticleId the catalog article id
	 * @return the article
	 */
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
	
	/**
	 * Gets the page.
	 *
	 * @param key the key
	 * @return the page
	 */
	public CatalogPage getPage(String key) {
		return getContext().loadChildObject(CatalogPage.class, this, key);
	}

	/**
	 * Gets the articles.
	 *
	 * @return the articles
	 */
	private List<CatalogArticle> getArticles() {
		if (articles == null) {
			articles = new ArrayList<CatalogArticle>();
			for (CatalogPage page : getPages()) {
				articles.addAll(page.getAllArticles()); 
			}
		}
		return articles;
	}
	
	/**
	 * Gets the catalog.
	 *
	 * @return the catalog
	 */
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

	/**
	 * Load.
	 */
	private static void load() {
		try {
			instance = AppContext.getSystemContext().loadObject(Catalog.class, Config.getString("nicki.catalog"));
		} catch (InvalidPrincipalException e) {
			log.error("Error", e);
			instance = null;
		}
	}

	/**
	 * Gets the referenced page.
	 *
	 * @param referencedPage the referenced page
	 * @return the referenced page
	 */
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

	/**
	 * Gets the articles for path.
	 *
	 * @param path the path
	 * @return the articles for path
	 */
	public List<CatalogArticle> getArticlesForPath(String path) {
		List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
		for (CatalogArticle article : getAllArticles()) {
			if (StringUtils.equalsIgnoreCase(article.getArticlePath(), path)) {
				catalogArticles.add(article);
			}
		}
		return catalogArticles;
	}

	/**
	 * Gets the pages.
	 *
	 * @return the pages
	 */
	public List<CatalogPage> getPages() {
		if (pages == null) {
			pages = getContext().loadChildObjects(CatalogPage.class, this.getPath(), null);
			for (CatalogPage page : pages) {
				try {
					getContext().loadObject(page);
				} catch (DynamicObjectException e) {
					log.error("Error", e);
				}
			}
		}
		return pages;
	}
	
	/**
	 * Gets the all articles.
	 *
	 * @param person the person
	 * @return the all articles
	 */
	public List<CatalogArticle> getAllArticles(Person person) {
		List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
		for (CatalogArticle catalogArticle : getAllArticles()) {
			if (hasArticle(person, catalogArticle)) {
				catalogArticles.add(catalogArticle);
			}
		}
		return catalogArticles;
	}

	/**
	 * Gets the all articles.
	 *
	 * @return the all articles
	 */
	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
		for (CatalogArticle catalogArticle : getArticles()) {
			catalogArticles.add(catalogArticle);
		}
		return catalogArticles;
	}


	/**
	 * Checks for article.
	 *
	 * @param person the person
	 * @param article the article
	 * @return true, if successful
	 */
	public boolean hasArticle(Person person, CatalogArticle article) {
		return article.hasArticle(person, article);
	}

	/**
	 * Gets the articles.
	 *
	 * @param path the path
	 * @return the articles
	 */
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

	/**
	 * Gets the articles of type.
	 *
	 * @param catalogArticle the catalog article
	 * @return the articles of type
	 */
	public List<CatalogArticle> getArticlesOfType(CatalogArticle catalogArticle) {
		List<CatalogArticle> result = new ArrayList<CatalogArticle>();
		for (CatalogArticle article : getAllArticles()) {
			if (catalogArticle.getClass().isAssignableFrom(article.getClass())) {
				result.add(article);
			}
		}
		return result;
	}

	/**
	 * Gets the child list.
	 *
	 * @return the child list
	 */
	@Override
	public List<? extends CatalogObject> getChildList() {
		return getPages();
	}

}
