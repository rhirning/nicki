
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.mgnl.nicki.core.annotation.Child;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.types.TextArea;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class CatalogPage.
 */
@SuppressWarnings("serial")
@Slf4j
@DynamicObject
@ObjectClass("nickiCatalogPage")
@Child(name="child", objectFilter={CatalogPage.class, CatalogArticle.class})
public class CatalogPage extends CatalogObject {
	
	/** The provider. */
	private Provider provider;

	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/** The category. */
	@DynamicAttribute(externalName="nickiCategory")
	private String[] category;
	
	/** The provider class. */
	@DynamicAttribute(externalName="nickiProvider")
	private String providerClass;
	
	/** The provider data. */
	@DynamicAttribute(externalName="nickiProviderData")
	private String providerData;
	
	/** The attributes. */
	@DynamicAttribute(externalName="nickiAttributes")
	private TextArea attributes;
	

	/** The pages. */
	private List<CatalogPage> pages;
	
	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public List<CatalogArticleAttribute> getAttributes() {
		List<CatalogArticleAttribute> list = new ArrayList<CatalogArticleAttribute>();
		String attributes = getAttribute("attributes");
		if (StringUtils.isNotEmpty(attributes)) {
			try {
				Document document = XMLHelper.documentFromString(attributes);
				List<Element> attrs = document.getRootElement().getChildren("attribute");
				if (attrs != null) {
					for (Element attributeElement : attrs) {
						list.add(new CatalogArticleAttribute(attributeElement));
					}
				}
			} catch (JDOMException e) {
				log.error("Error", e);
			} catch (IOException e) {
				log.error("Error", e);
			}
		}
		return list;
	}
	
	/**
	 * Gets the inherited attributes.
	 *
	 * @return the inherited attributes
	 */
	public List<CatalogArticleAttribute> getInheritedAttributes() {
		try {
			return getParent(CatalogPage.class).getAllAttributes();
		} catch (Exception e) {
			return new ArrayList<CatalogArticleAttribute>();
		}
	}

	/**
	 * Gets the all attributes.
	 *
	 * @return the all attributes
	 */
	public List<CatalogArticleAttribute> getAllAttributes() {
		List<CatalogArticleAttribute> list = getAttributes();
		list.addAll(getInheritedAttributes());
		return list;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param attributes the new attributes
	 */
	public void setAttributes(List<String> attributes) {
		put("attribute", attributes);
	}

	/**
	 * Checks for attributes.
	 *
	 * @return true, if successful
	 */
	public boolean hasAttributes() {
		return getAttributes() != null && getAttributes().size() != 0;
	}

	/**
	 * Checks for categories.
	 *
	 * @return true, if successful
	 */
	public boolean hasCategories() {
		return getCategories() != null && getCategories().size() != 0;
	}

	/**
	 * Gets the categories.
	 *
	 * @return the categories
	 */
	@SuppressWarnings("unchecked")
	public List<String> getCategories() {
		return (List<String>) get("category");
	}
	
	/**
	 * Gets the all categories.
	 *
	 * @return the all categories
	 */
	public List<String> getAllCategories() {
		List<String> categories = new ArrayList<String>();
		if (hasCategories()) {
			categories.addAll(getCategories());
		}
		categories.addAll(getInheritedCategories());
		return categories;
	}
	
	/**
	 * Gets the inherited categories.
	 *
	 * @return the inherited categories
	 */
	public List<String> getInheritedCategories() {
		try {
			return getParent(CatalogPage.class).getAllCategories();
		} catch (Exception e) {
			log.debug("Error", e);
		}
		return new ArrayList<String>();
	}
	
	/**
	 * Sets the categories.
	 *
	 * @param categories the new categories
	 */
	public void setCategories(List<String> categories) {
		put("category", categories);
	}
	
	/**
	 * Gets the articles.
	 *
	 * @return the articles
	 */
	public List<CatalogArticle> getArticles() {
		if (this.provider != null) {
			return provider.getArticles(this);
		} else {
			List<CatalogArticle> articles = getContext().loadChildObjects(CatalogArticle.class, getPath(), null);
			return articles;
		}
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
	 * @return the all articles
	 */
	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> articles = getArticles();

		for (CatalogPage page : getPages()) {
			articles.addAll(page.getAllArticles()); 
		}

		return articles;
	}

	/**
	 * Inits the.
	 *
	 * @param rs the rs
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@Override
	public void init(ContextSearchResult rs) throws DynamicObjectException {
		super.init(rs);
		String providerClass = getAttribute("providerClass");
		if (StringUtils.isNotEmpty(providerClass)) {
			try {
				this.provider = (Provider) Classes.newInstance(providerClass);
				this.provider.init(this);
			} catch (Exception e) {
				log.error("Error", e);
			}
		}
	}

	/**
	 * Gets the article.
	 *
	 * @param key the key
	 * @return the article
	 */
	public CatalogArticle getArticle(String key) {
		if (StringUtils.contains(key, Catalog.PATH_SEPARATOR)) {
			String pageKey = StringUtils.substringBefore(key, Catalog.PATH_SEPARATOR);
			String articleKey = StringUtils.substringAfter(key, Catalog.PATH_SEPARATOR);
			CatalogPage page = getPage(pageKey);
			if (page != null) {
				return page.getArticle(articleKey);
			} else {
				return null;
			}
		} else {
			if (this.provider != null){
				return provider.getArticle(key);
			} else {
				return getPageArticle(key);
			}
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
	 * Gets the page article.
	 *
	 * @param key the key
	 * @return the page article
	 */
	public CatalogArticle getPageArticle(String key) {
		return getContext().loadChildObject(CatalogArticle.class, this, key);
	}

	/**
	 * Gets the page articles.
	 *
	 * @return the page articles
	 */
	public List<CatalogArticle> getPageArticles() {
		return getContext().loadChildObjects(CatalogArticle.class, this, "");
	}

	/**
	 * Gets the catalog path.
	 *
	 * @return the catalog path
	 */
	public String getCatalogPath() {
		return getSlashPath(Catalog.getCatalog());
	}

	/**
	 * Gets the articles.
	 *
	 * @param key the key
	 * @return the articles
	 */
	public List<CatalogArticle> getArticles(String key) {
		if (StringUtils.contains(key, Catalog.PATH_SEPARATOR)) {
			String pageKey = StringUtils.substringBefore(key, Catalog.PATH_SEPARATOR);
			String articleKey = StringUtils.substringAfter(key, Catalog.PATH_SEPARATOR);
			CatalogPage page = getPage(pageKey);
			if (page != null) {
				return page.getArticles(articleKey);
			}
		} else {
			if ("*".equals(key)) {
				return getArticles();
			} else if ("**".equals(key)) {
				return getAllArticles();
			}
		}
		return null;

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
