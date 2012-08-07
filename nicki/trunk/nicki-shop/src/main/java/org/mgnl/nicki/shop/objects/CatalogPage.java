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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.DynamicTemplateObject;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

@SuppressWarnings("serial")
public class CatalogPage extends DynamicTemplateObject {
	
	private Provider provider = null;

	public void initDataModel() {
		addObjectClass("nickiCatalogPage");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("provider", "nickiProvider", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("attributes", "nickiAttributes", TextArea.class);
		addAttribute(dynAttribute);
		
		// TODO
		addChild("page", "objectClass=nickiCatalogPage");
		addChild("article", "objectClass=nickiCatalogArticle");

	};
	
	public List<CatalogArticleAttribute> getAttributes() {
		List<CatalogArticleAttribute> list = new ArrayList<CatalogArticleAttribute>();
		String attributes = getAttribute("attributes");
		if (StringUtils.isNotEmpty(attributes)) {
			try {
				Document document = XMLHelper.documentFromString(attributes);
				@SuppressWarnings("unchecked")
				List<Element> attrs = document.getRootElement().getChildren("attribute");
				if (attrs != null) {
					for (Iterator<Element> iterator = attrs.iterator(); iterator.hasNext();) {
						Element attributeElement = iterator.next();
						list.add(new CatalogArticleAttribute(attributeElement));
					}
				}
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> articles = getArticles();
		try {
			TemplateMethodModel method = (TemplateMethodModel) get("getPage");
			if (method != null) {
				@SuppressWarnings("unchecked")
				List<Object> pages = (List<Object>) method.exec(null);
				for (Iterator<Object> iterator = pages.iterator(); iterator
						.hasNext();) {
					CatalogPage page= (CatalogPage) iterator.next();
					articles.addAll(page.getAllArticles()); 
				}
			}
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		return articles;
	}

	@Override
	public void init(ContextSearchResult rs) throws DynamicObjectException {
		super.init(rs);
		String providerClass = getAttribute("provider");
		if (StringUtils.isNotEmpty(providerClass)) {
			try {
				this.provider = (Provider) Classes.newInstance(providerClass);
				this.provider.init(this);
			} catch (Exception e) {
				e.printStackTrace();
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



}
