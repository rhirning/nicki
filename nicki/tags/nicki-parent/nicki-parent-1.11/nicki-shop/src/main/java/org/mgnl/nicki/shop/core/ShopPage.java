package org.mgnl.nicki.shop.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

public class ShopPage {
	private Shop shop;
	private TYPE type;
	private String name;
	private String label;
	private List<ShopPage> pageList = new ArrayList<ShopPage>();
	private List<CatalogArticle> articleList = new ArrayList<CatalogArticle>();
	private List<CatalogArticleAttribute> attributeList = new ArrayList<CatalogArticleAttribute>();
	private String renderer;

	public ShopPage(Shop shop, Element pageElement) {
		this.shop = shop;
		this.type = getPageType(pageElement);
		
		if (type == TYPE.TYPE_PERSON_DATA) {
			this.name = pageElement.getAttributeValue("name");
			this.label = I18n.getText(pageElement.getAttributeValue("label"));
			@SuppressWarnings("unchecked")
			List<Element> attributes = pageElement.getChildren("attribute");
			if (attributes != null && attributes.size() > 0) {
				for (Iterator<Element> iterator = attributes.iterator(); iterator.hasNext();) {
					Element attributeElement = iterator.next();
					attributeList.add(new CatalogArticleAttribute(attributeElement));
				}
			}
		} else if (type == TYPE.TYPE_SHOP_ARTICLE_PAGE) {
			String catalogArticleId = pageElement.getAttributeValue("article");
			if (StringUtils.isNotEmpty(catalogArticleId)) {
				CatalogArticle article = this.shop.getArticle(catalogArticleId);
				this.name = article.getName();
				this.label = article.getDisplayName();
				this.articleList.add(article);
			}
		} else if (type == TYPE.TYPE_STRUCTURE_PAGE) {
			this.type = TYPE.TYPE_STRUCTURE_PAGE;
			this.renderer = pageElement.getAttributeValue("renderer");
			this.name = pageElement.getAttributeValue("name");
			this.label = I18n.getText(pageElement.getAttributeValue("label"));
			@SuppressWarnings("unchecked")
			List<Element> pages = pageElement.getChildren("page");
			if (pages != null && pages.size() > 0) {
				for (Iterator<Element> iterator = pages.iterator(); iterator.hasNext();) {
					Element pElement = iterator.next();
					this.pageList.add(new ShopPage(shop, pElement));
				}
			}
			@SuppressWarnings("unchecked")
			List<Element> articles = pageElement.getChildren("article");
			if (articles != null && articles.size() > 0) {
				for (Iterator<Element> iterator = articles.iterator(); iterator.hasNext();) {
					Element articleElement = iterator.next();
					String catalogArticleId = articleElement.getAttributeValue("article");
					if (StringUtils.isNotEmpty(catalogArticleId)) {
						CatalogArticle article = this.shop.getArticle(catalogArticleId);
						this.articleList.add(article);
					}
				}
			}
		}
	}

	private TYPE getPageType(Element pageElement) {
		String typeName = pageElement.getAttributeValue("type");
		if (StringUtils.isNotEmpty(typeName)) {
			return TYPE.getType(typeName);
		}
		String article = pageElement.getAttributeValue("article"); 
		if (StringUtils.isNotEmpty(article)) {
			return TYPE.TYPE_SHOP_ARTICLE_PAGE;
		}
		return TYPE.TYPE_STRUCTURE_PAGE;
		
	}

	public enum TYPE {
		TYPE_SHOP_ARTICLE_PAGE("shopArticlePage"),
		TYPE_STRUCTURE_PAGE("structurePage"),
		TYPE_PERSON_DATA("personData");

		String name;
		TYPE(String name) {
			this.name = name;
		}
		public static TYPE getType(String name) {
			if ("shopArticlePage".equals(name)) {
				return TYPE_SHOP_ARTICLE_PAGE;
			} else if ("structurePage".equals(name)) {
				return TYPE_STRUCTURE_PAGE;
			} else if ("personData".equals(name)) {
				return TYPE_PERSON_DATA;
			} else {
				return null;
			}
		}
	}


	public String getName() {
		return name;
	}


	public String getLabel() {
		return label;
	}

	public String getRenderer() {
		return renderer;
	}

	public TYPE getType() {
		return type;
	}

	public boolean hasAttributes() {
		return this.attributeList != null && this.attributeList.size() > 0;
	}

	public List<CatalogArticleAttribute> getAttributeList() {
		return attributeList;
	}

	public boolean hasArticles() {
		return this.articleList != null && this.articleList.size() > 0;
	}

	public List<CatalogArticle> getArticleList() {
		return articleList;
	};

}
