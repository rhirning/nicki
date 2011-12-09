/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.core;

import java.util.List;

import org.mgnl.nicki.shop.catalog.CatalogArticle;

public interface ShopViewerComponent {
	ShopViewerComponent getShopViewerComponent();

	List<ShopPage> getPageList();

	List<CatalogArticle> getArticles();

	List<CatalogArticle> getAllArticles();

}
