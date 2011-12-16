/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.shop.catalog;

import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public interface CatalogArticleSelector {

	void setShopper(Person shopper);

	void setRecipient(Person recipient);

	List<CatalogArticle> getArticles();

}
