package org.mgnl.nicki.vaadin.base.shop.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.catalog.Catalog;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.shop.catalog.CatalogArticleSelector;

@SuppressWarnings("serial")
public class ShopPage implements ShopViewerComponent, Serializable{
	private Shop shop;
	private TYPE type;
	private String name;
	private String label;
	private String referencedPage;
	private List<ShopPage> pageList = new ArrayList<ShopPage>();
	private List<CatalogArticle> articleList = new ArrayList<CatalogArticle>();
	private List<CatalogArticleAttribute> attributeList = new ArrayList<CatalogArticleAttribute>();
	private String renderer;
	private String selectorClass;

	public ShopPage(Shop shop, Element pageElement) {
		this.shop = shop;
		this.type = getPageType(pageElement);
		
		if (type == TYPE.SHOP_ARTICLE_PAGE) {
			String catalogArticleId = pageElement.getAttributeValue("article");
			if (StringUtils.isNotEmpty(catalogArticleId)) {
				CatalogArticle article = this.shop.getArticle(catalogArticleId);
				this.name = article.getName();
				this.label = article.getDisplayName();
				this.articleList.add(article);
			}
		} else if (type == TYPE.STRUCTURE_PAGE) {
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
		} else if (type == TYPE.PAGE_REF_PAGE) {
			this.renderer = pageElement.getAttributeValue("renderer");
			this.name = pageElement.getAttributeValue("name");
			this.label = I18n.getText(pageElement.getAttributeValue("label"));
			this.referencedPage = pageElement.getAttributeValue("page");
			this.articleList.addAll(Catalog.getCatalog().getReferencedPage(referencedPage).getArticles());
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
		} else if (type == TYPE.ARTICLE_SELECT) {
			this.renderer = pageElement.getAttributeValue("renderer");
			this.name = pageElement.getAttributeValue("name");
			this.label = I18n.getText(pageElement.getAttributeValue("label"));
			this.selectorClass = pageElement.getAttributeValue("selector");
			try {
				CatalogArticleSelector selector = (CatalogArticleSelector) Class.forName(selectorClass).newInstance();
				selector.setShopper(shop.getShopper());
				selector.setRecipient(shop.getRecipient());
				List<CatalogArticle> articles = selector.getArticles();
				if (articles != null && articles.size() > 0) {
					this.articleList.addAll(articles);
				}
			} catch (Exception e) {
				e.printStackTrace();
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
			return TYPE.SHOP_ARTICLE_PAGE;
		}
		return TYPE.STRUCTURE_PAGE;
		
	}

	public enum TYPE {
		SHOP_ARTICLE_PAGE("shopArticlePage"),
		STRUCTURE_PAGE("structurePage"),
		PAGE_REF_PAGE("pageRef"),
		ARTICLE_SELECT("select");

		String name;
		TYPE(String name) {
			this.name = name;
		}
		public static TYPE getType(String name) {
			if ("shopArticlePage".equals(name)) {
				return SHOP_ARTICLE_PAGE;
			} else if ("structurePage".equals(name)) {
				return STRUCTURE_PAGE;
			} else if ("pageRef".equals(name)) {
				return PAGE_REF_PAGE;
			} else if ("select".equals(name)) {
				return ARTICLE_SELECT;
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

	public boolean hasPages() {
		return this.pageList != null && this.pageList.size() > 0;
	}

	public List<CatalogArticle> getArticleList() {
		return articleList;
	};
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[page name='").append(getName());
		sb.append("' label='").append(getLabel());
		sb.append("' renderer='").append(getRenderer());
		sb.append("']\n");
		if (hasPages()) {
			for (Iterator<ShopPage> iterator = pageList.iterator(); iterator.hasNext();) {
				ShopPage page = iterator.next();
				sb.append(page.toString()).append("\n");
			}
		}
		if (hasArticles()) {
			for (Iterator<CatalogArticle> iterator = articleList.iterator(); iterator.hasNext();) {
				CatalogArticle article = iterator.next();
				sb.append(article.toString()).append("\n");
			}
		}
		if (hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = attributeList.iterator(); iterator.hasNext();) {
				CatalogArticleAttribute attribute = iterator.next();
				sb.append(attribute.toString()).append("\n");
			}
		}
		return sb.toString();
	}

	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		if (hasArticles()) {
			articles.addAll(articleList);
		}
		if (hasPages()) {
			for (Iterator<ShopPage> iterator = pageList.iterator(); iterator.hasNext();) {
				ShopPage page = iterator.next();
				articles.addAll(page.getArticles());
			}
		}
		return articles;
	}

	@Override
	public ShopViewerComponent getShopViewerComponent() {
		return this;
	}

	@Override
	public List<ShopPage> getPageList() {
		return pageList;
	}

	@Override
	public List<CatalogArticle> getArticles() {
		return articleList;
	}

}
