package org.mgnl.nicki.dynamic.objects.shop;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.ldap.context.ObjectFactory;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;

public class CatalogArticleFactory {
	private static CatalogArticleFactory instance = null;
	private List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
	
	public static CatalogArticleFactory getInstance() throws InvalidPrincipalException, InstantiateDynamicObjectException {
		if (instance == null) {
			new CatalogArticleFactory();
		}
		return instance;
	}

	public CatalogArticleFactory() throws InvalidPrincipalException, InstantiateDynamicObjectException {
		ObjectFactory objectFactory = AppContext.getSystemContext().getObjectFactory();
		for (CatalogArticle catalogArticle : objectFactory.findDynamicObjects(CatalogArticle.class)) {
			articles.add(catalogArticle);
		};
	}

	public List<CatalogArticle> getArticles() {
		return articles;
	}
	
	
}
