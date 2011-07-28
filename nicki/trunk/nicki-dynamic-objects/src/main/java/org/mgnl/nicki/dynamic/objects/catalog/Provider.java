package org.mgnl.nicki.dynamic.objects.catalog;

import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogPage;

public interface Provider {

	List<CatalogArticle> getArticles(CatalogPage catalogPage);

	CatalogArticle getArticle(String key);

	void init(CatalogPage catalogPage);

}
