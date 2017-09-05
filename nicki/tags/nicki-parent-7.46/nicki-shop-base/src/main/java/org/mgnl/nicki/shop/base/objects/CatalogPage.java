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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mgnl.nicki.core.annotation.Child;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")

@DynamicObject
@ObjectClass("nickiCatalogPage")
@Child(name="child", objectFilter={CatalogPage.class, CatalogArticle.class})
public class CatalogPage extends CatalogObject {
	private static final Logger LOG = LoggerFactory.getLogger(CatalogPage.class);
	
	private Provider provider;

	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@DynamicAttribute(externalName="nickiCategory")
	private String[] category;
	@DynamicAttribute(externalName="nickiProvider")
	private String providerClass;
	@DynamicAttribute(externalName="nickiProviderData")
	private String providerData;
	@DynamicAttribute(externalName="nickiAttributes")
	private TextArea attributes;
	

	private List<CatalogPage> pages;
	
	public List<CatalogArticleAttribute> getAttributes() {
		List<CatalogArticleAttribute> list = new ArrayList<CatalogArticleAttribute>();
		String attributes = getAttribute("attributes");
		if (StringUtils.isNotEmpty(attributes)) {
			try {
				Document document = XMLHelper.documentFromString(attributes);
				@SuppressWarnings("unchecked")
				List<Element> attrs = document.getRootElement().getChildren("attribute");
				if (attrs != null) {
					for (Element attributeElement : attrs) {
						list.add(new CatalogArticleAttribute(attributeElement));
					}
				}
			} catch (JDOMException e) {
				LOG.error("Error", e);
			} catch (IOException e) {
				LOG.error("Error", e);
			}
		}
		return list;
	}
	
	public List<CatalogArticleAttribute> getInheritedAttributes() {
		try {
			return getParent(CatalogPage.class).getAllAttributes();
		} catch (Exception e) {
			return new ArrayList<CatalogArticleAttribute>();
		}
	}

	public List<CatalogArticleAttribute> getAllAttributes() {
		List<CatalogArticleAttribute> list = getAttributes();
		list.addAll(getInheritedAttributes());
		return list;
	}

	public void setAttributes(List<String> attributes) {
		put("attribute", attributes);
	}

	public boolean hasAttributes() {
		return getAttributes() != null && getAttributes().size() != 0;
	}

	public boolean hasCategories() {
		return getCategories() != null && getCategories().size() != 0;
	}

	@SuppressWarnings("unchecked")
	public List<String> getCategories() {
		return (List<String>) get("category");
	}
	
	public List<String> getAllCategories() {
		List<String> categories = new ArrayList<String>();
		if (hasCategories()) {
			categories.addAll(getCategories());
		}
		categories.addAll(getInheritedCategories());
		return categories;
	}
	
	public List<String> getInheritedCategories() {
		try {
			return getParent(CatalogPage.class).getAllCategories();
		} catch (Exception e) {
			LOG.debug("Error", e);
		}
		return new ArrayList<String>();
	}
	
	public void setCategories(List<String> categories) {
		put("category", categories);
	}
	
	public List<CatalogArticle> getArticles() {
		if (this.provider != null) {
			return provider.getArticles(this);
		} else {
			List<CatalogArticle> articles = getContext().loadChildObjects(CatalogArticle.class, getPath(), null);
			return articles;
		}
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


	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> articles = getArticles();

		for (CatalogPage page : getPages()) {
			articles.addAll(page.getAllArticles()); 
		}

		return articles;
	}

	@Override
	public void init(ContextSearchResult rs) throws DynamicObjectException {
		super.init(rs);
		String providerClass = getAttribute("providerClass");
		if (StringUtils.isNotEmpty(providerClass)) {
			try {
				this.provider = (Provider) Classes.newInstance(providerClass);
				this.provider.init(this);
			} catch (Exception e) {
				LOG.error("Error", e);
			}
		}
	}

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
	
	public CatalogPage getPage(String key) {
		return getContext().loadChildObject(CatalogPage.class, this, key);
	}

	public CatalogArticle getPageArticle(String key) {
		return getContext().loadChildObject(CatalogArticle.class, this, key);
	}

	public List<CatalogArticle> getPageArticles() {
		return getContext().loadChildObjects(CatalogArticle.class, this, "");
	}

	public String getCatalogPath() {
		return getSlashPath(Catalog.getCatalog());
	}

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

	@Override
	public List<? extends CatalogObject> getChildList() {
		return getPages();
	}



}
