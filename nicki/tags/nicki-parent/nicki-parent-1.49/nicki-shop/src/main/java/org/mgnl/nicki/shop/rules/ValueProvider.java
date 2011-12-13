/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.shop.rules;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.Selector;

public interface ValueProvider {
	public enum TYPE {SELF,ALL};

	void init(Selector selector, String i18nBase);

	String getValue();

	String getPersonQuery(CatalogArticle article, String value);

	String getArticleQuery(Person person, Object value);

}
