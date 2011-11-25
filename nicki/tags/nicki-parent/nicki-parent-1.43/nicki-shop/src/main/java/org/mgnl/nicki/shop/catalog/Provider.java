package org.mgnl.nicki.shop.catalog;

import java.util.List;


public interface Provider {

	List<CatalogArticle> getArticles(CatalogPage catalogPage);

	CatalogArticle getArticle(String key);

	void init(CatalogPage catalogPage);

}
