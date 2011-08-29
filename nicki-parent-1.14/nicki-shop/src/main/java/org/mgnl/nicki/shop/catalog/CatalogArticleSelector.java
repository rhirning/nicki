package org.mgnl.nicki.shop.catalog;

import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public interface CatalogArticleSelector {

	void setShopper(Person shopper);

	void setRecipient(Person recipient);

	List<CatalogArticle> getArticles();

}
