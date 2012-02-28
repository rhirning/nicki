package org.mgnl.nicki.dynamic.objects.shop;

import java.util.ArrayList;
import java.util.List;

public class EmptyProvider implements Provider {

	@Override
	public List<CatalogArticle> getArticles(CatalogPage catalogPage) {
		return new ArrayList<CatalogArticle>();
	}

	@Override
	public CatalogArticle getArticle(String key) {
		return null;
	}

	@Override
	public void init(CatalogPage catalogPage) {
	}

}
