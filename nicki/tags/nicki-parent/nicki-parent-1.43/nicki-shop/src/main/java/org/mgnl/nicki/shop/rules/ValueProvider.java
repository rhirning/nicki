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
