/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.shop;

import java.util.List;


public interface Provider {

	List<CatalogArticle> getArticles(CatalogPage catalogPage);

	CatalogArticle getArticle(String key);

	void init(CatalogPage catalogPage);

}
