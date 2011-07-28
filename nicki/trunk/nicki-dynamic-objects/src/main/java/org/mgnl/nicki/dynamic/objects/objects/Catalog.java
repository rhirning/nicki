package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.ldap.methods.LoadObjectsMethod;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

@SuppressWarnings("serial")
public class Catalog extends DynamicTemplateObject {
	public static final String PATH_SEPARATOR = "/";

	public void initDataModel() {
		// objectClass
		addObjectClass("nickiCatalog");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		addChild("page", "objectClass=nickiCatalogPage");
		
		addMethod("allArticles", new LoadObjectsMethod(getContext(), this, "objectClass=nickiCatalogArticle"));

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

	public List<CatalogArticle> getAllArticles() {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
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
	
	public static Catalog getCatalog() {
		try {
			return AppContext.getSystemContext().loadObject(Catalog.class, Config.getProperty("nicki.catalog"));
		} catch (InvalidPrincipalException e) {
			e.printStackTrace();
			return null;
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

}
