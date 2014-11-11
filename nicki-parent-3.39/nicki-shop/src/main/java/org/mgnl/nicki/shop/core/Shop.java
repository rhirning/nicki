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
package org.mgnl.nicki.shop.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;

@SuppressWarnings("serial")
public class Shop implements ShopViewerComponent, Serializable {
	private String renderer;
	private List<ShopPage> pageList = new ArrayList<ShopPage>();
	private Document document = null;
	private Person shopper;
	private Person recipient;

	public Shop (String classLoaderPath, Person shopper, Person recipient) throws JDOMException, IOException {
		document = XMLHelper.documentFromClasspath(this.getClass(), classLoaderPath);
		this.shopper = shopper;
		this.recipient = recipient;
		load();
	}
	
	private void load() {
		Element root = document.getRootElement();
		
		this.setRenderer(StringUtils.trimToNull(root.getAttributeValue("renderer")));
		
		@SuppressWarnings("unchecked")
		List<Element> pages = root.getChildren("page");
		if (pages != null && pages.size() > 0) {
			for (Element pageElement : pages) {
				pageList.add(new ShopPage(this, pageElement));
			}
		}
		
	}

	public Document getDocument() {
		return document;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (ShopPage page : pageList) {
			sb.append(page.toString()).append("\n");
		}
		return sb.toString();
	}

	public CatalogArticle getArticle(String catalogArticleId) {
		return Catalog.getCatalog().getArticle(catalogArticleId);
	}

	public List<ShopPage> getPageList() {
		return pageList;
	}

	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}

	public String getRenderer() {
		return renderer;
	}

	public boolean hasPages() {
		return pageList != null && pageList.size() > 0;
	}

	public List<CatalogArticle> getArticles() {
		return new ArrayList<CatalogArticle>();
	}

	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		if (hasPages()) {
			for (ShopPage page : getPageList()) {
				articles.addAll(page.getArticles());
			}
		}
		return articles;
	}

	public ShopViewerComponent getShopViewerComponent() {
		return this;
	}


	public void setRecipient(Person recipient) {
		this.recipient = recipient;
	}

	public Person getRecipient() {
		return recipient;
	}

	public void setShopper(Person shopper) {
		this.shopper = shopper;
	}

	public Person getShopper() {
		return shopper;
	}

}
