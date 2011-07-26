package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.ldap.methods.LoadObjectsMethod;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

@SuppressWarnings("serial")
public class Catalog extends DynamicTemplateObject {

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

	public CatalogArticle getArticle(String catalogArticleId) {
		CatalogArticle article = getContext().loadObject(CatalogArticle.class, catalogArticleId + "," + getPath());
		if (article != null) {
			article.setCatalog(this);
		}
		return article;
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

}
