package org.mgnl.nicki.shop.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.objects.Catalog;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;

@SuppressWarnings("serial")
public class Shop implements Serializable {
	private Person person;
	private Catalog catalog;
	private List<ShopPage> pageList = new ArrayList<ShopPage>();
	Document document = null;

	public Shop (String classLoaderPath) throws JDOMException, IOException {
		document = XMLHelper.documentFromClasspath(this.getClass(), classLoaderPath);
		load();
	}
	
	private void load() {
		Element root = document.getRootElement();
		String catalogName = root.getAttributeValue("catalog");
		try {
			this.catalog = AppContext.getSystemContext().loadObject(Catalog.class,
					"cn=" + catalogName + "," + Config.getProperty("nicki.catalogs.basedn"));
		} catch (InvalidPrincipalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		return catalog.getArticle(catalogArticleId);
	}

	public List<ShopPage> getPageList() {
		return pageList;
	}

	public void setPerson (Person person) {
		this.person = person;
	}
}
