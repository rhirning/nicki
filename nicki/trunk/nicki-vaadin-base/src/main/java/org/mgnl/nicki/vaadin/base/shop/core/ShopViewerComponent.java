package org.mgnl.nicki.vaadin.base.shop.core;

import java.util.List;

import org.mgnl.nicki.shop.catalog.CatalogArticle;

public interface ShopViewerComponent {
	ShopViewerComponent getShopViewerComponent();

	List<ShopPage> getPageList();

	List<CatalogArticle> getArticles();

	List<CatalogArticle> getAllArticles();

}
