package org.mgnl.nicki.vaadin.base.shop;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.objects.Catalog;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;

@SuppressWarnings("serial")
public class Shop implements ShopViewerComponent, Serializable {
	private String renderer;
	private List<ShopPage> pageList = new ArrayList<ShopPage>();
	Document document = null;

	public Shop (String classLoaderPath) throws JDOMException, IOException {
		document = XMLHelper.documentFromClasspath(this.getClass(), classLoaderPath);
		load();
	}
	
	private void load() {
		Element root = document.getRootElement();
		
		this.setRenderer(StringUtils.trimToNull(root.getAttributeValue("renderer")));
		
		@SuppressWarnings("unchecked")
		List<Element> pages = root.getChildren("page");
		if (pages != null && pages.size() > 0) {
			for (Iterator<Element> iterator = pages.iterator(); iterator.hasNext();) {
				Element pageElement = iterator.next();
				pageList.add(new ShopPage(this, pageElement));
			}
		}
		
	}

	public Document getDocument() {
		return document;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Iterator<ShopPage> iterator = pageList.iterator(); iterator.hasNext();) {
			ShopPage page = iterator.next();
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
			for (Iterator<ShopPage> iterator = getPageList().iterator(); iterator.hasNext();) {
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

}
