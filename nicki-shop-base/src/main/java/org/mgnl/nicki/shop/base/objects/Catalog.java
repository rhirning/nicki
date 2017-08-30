/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.shop.base.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.Child;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@DynamicObject
@ObjectClass("nickiCatalog")
@Child(name="child", objectFilter={CatalogPage.class})
public class Catalog extends CatalogObject{
	private static final Logger LOG = LoggerFactory.getLogger(Catalog.class);
	private static final long serialVersionUID = 1114608130611536361L;
	public static final String PATH_SEPARATOR = "/";
	private static long lastBuild = 0;
	private static long buildInterval = 10*60*1000; // ms
	private static Catalog instance;
	
	private List<CatalogPage> pages;
	private List<CatalogArticle> articles;

	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	@DynamicAttribute(externalName="nickiCategory")
	private String[] category;
	
	public Catalog() {
		
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
			for (CatalogPage page : getPages()) {
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
			LOG.error("Error", e);
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
		for (CatalogArticle article : getAllArticles()) {
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
					LOG.error("Error", e);
				}
			}
		}
		return pages;
	}
	
	public List<CatalogArticle> getAllArticles(Person person) {
		List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
		for (CatalogArticle catalogArticle : getAllArticles()) {
			if (hasArticle(person, catalogArticle)) {
				catalogArticles.add(catalogArticle);
			}
		}
		return catalogArticles;
	}

	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
		for (CatalogArticle catalogArticle : getArticles()) {
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

	@Override
	public List<? extends CatalogObject> getChildList() {
		return getPages();
	}

}
