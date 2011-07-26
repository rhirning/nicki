package org.mgnl.nicki.vaadin.base.shop;

import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;

public interface ShopViewerComponent {
	ShopViewerComponent getShopViewerComponent();

	List<ShopPage> getPageList();

	List<CatalogArticle> getArticles();

	List<CatalogArticle> getAllArticles();

}
