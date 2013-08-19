package org.mgnl.nicki.shop.objects;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogPage;
import org.mgnl.nicki.shop.base.objects.Provider;

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
