/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogArticleAttribute;
import org.mgnl.nicki.shop.renderer.ShopRenderer;
import org.mgnl.nicki.shop.renderer.TableRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ShopPage
 * 
 * Represents a page or a container in the shop
 * 
 * @author rhirning
 *
 * Attributes:
 * 
 * name		will not be used, only to make the XML readable
 * label	i18n Key for the page
 * article	id of article in the page
 * renderer	renderer class to render the page (Default: org.mgnl.nicki.shop.renderer.TableRenderer)
 *
 * Different types of ShopPage declarations:
 * 1. Page withone article
 * 
 * <page name="multi" article="/Host/ChangeMan fuer FB-Freigeber"/>
 * 
 * The page uses the label of the article
 * 
 * 2. Page with declared articles (in sub tags)
 *	
 *	<page name="notes" label="pnw.gui.rights.pages.Notes" renderer="org.mgnl.nicki.shop.renderer.TabRenderer">
 *		<articles type="pageRef" page="/Notes"/>
 *	</page>
 *  
 */
@SuppressWarnings("serial")
public class ShopPage implements ShopViewerComponent, Serializable {
	private static final Logger LOG =  LoggerFactory.getLogger(ShopPage.class);
	private String name;
	private String label;
	private List<ShopPage> pageList = new ArrayList<ShopPage>();
	private List<CatalogArticleAttribute> attributeList = new ArrayList<CatalogArticleAttribute>();
	private String renderer;
	private List<ArticleContainer> shopArticles = new ArrayList<ArticleContainer>();
	List<CatalogArticle> articles;

	public ShopPage(Shop shop, Element pageElement) {
		
		this.name = pageElement.getAttributeValue("name");
		this.label = I18n.getText(pageElement.getAttributeValue("label"));
		this.renderer = pageElement.getAttributeValue("renderer");
		if (StringUtils.isBlank(renderer)) {
			renderer = TableRenderer.class.getCanonicalName();
		}
		
		for (Object child  : pageElement.getChildren()) {
			if (child instanceof Element) {
				Element childElement = (Element) child;
				if (StringUtils.equals("page", childElement.getName())) {
					pageList.add(new ShopPage(shop, childElement));
				}
			}
		}
		
		// direct article
		String catalogArticleId = pageElement.getAttributeValue("article");
		if (StringUtils.isNotBlank(catalogArticleId)) {
			CatalogArticle catalogArticle = Catalog.getCatalog().getArticle(catalogArticleId);
			if (catalogArticle != null) {
				ArticleContainer articleContainer = new ArticleContainer();
				articleContainer.addArticle(catalogArticle);
				this.shopArticles.add(articleContainer);
				this.name = catalogArticle.getName();
				this.label = catalogArticle.getDisplayName();
			}
		}
		
		// articles
		@SuppressWarnings("unchecked")
		List<Element> articlesElements = pageElement.getChildren("articles");
		if (articlesElements != null && articlesElements.size() > 0) {
			for (Element articlesElement : articlesElements) {
				this.shopArticles.add(new ArticleContainer(shop, articlesElement));
			}
		}
		
	}


	public String getName() {
		return name;
	}


	public String getLabel() {
		return label;
	}

	public boolean hasAttributes() {
		return this.attributeList != null && this.attributeList.size() > 0;
	}

	public List<CatalogArticleAttribute> getAttributeList() {
		return attributeList;
	}

	public boolean hasPages() {
		return this.pageList != null && this.pageList.size() > 0;
	}

	public List<CatalogArticle> getArticles() {
		if (articles == null) {
			articles = new ArrayList<CatalogArticle>();
			for (ArticleContainer container : shopArticles) {
				articles.addAll(container.getArticles());
			}
		}
		return articles;
	};
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[page name='").append(getName());
		sb.append("' label='").append(getLabel());
		sb.append("' renderer='").append(getRenderer());
		sb.append("']\n");
		if (hasPages()) {
			for (ShopPage page : pageList) {
				sb.append(page.toString()).append("\n");
			}
		}
		if (!getArticles().isEmpty()) {
			for (CatalogArticle article : getArticles()) {
				sb.append(article.toString()).append("\n");
			}
		}
		if (hasAttributes()) {
			for (CatalogArticleAttribute attribute : attributeList) {
				sb.append(attribute.toString()).append("\n");
			}
		}
		return sb.toString();
	}

	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		articles.addAll(getArticles());
		if (hasPages()) {
			for (ShopPage page : pageList) {
				articles.addAll(page.getAllArticles());
			}
		}
		return articles;
	}

	public ShopViewerComponent getShopViewerComponent() {
		return this;
	}

	public List<ShopPage> getPageList() {
		return pageList;
	}
	

	
	public ShopRenderer getRenderer() {
		try {
			return (ShopRenderer) Classes.newInstance(renderer);
		} catch (Exception e) {
			LOG.debug("Error", e);
		}
		return new TableRenderer();
	}

}
