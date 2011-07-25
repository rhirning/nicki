package org.mgnl.nicki.dynamic.objects.objects;

import org.mgnl.nicki.ldap.methods.LoadObjectsMethod;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

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

}
