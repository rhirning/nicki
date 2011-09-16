package org.mgnl.nicki.shop.rules;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticle;

public class ChangeSet {
	private List<ArticleChange> missing = new ArrayList<ArticleChange>();
	private List<ArticleChange> surplus = new ArrayList<ArticleChange>();

	public void addToMissing(Person person, CatalogArticle article) {
		missing.add(new ArticleChange(ArticleChange.TYPE.ADD, person, article));
	}
	public void addToSurplus(Person person, CatalogArticle article) {
		surplus.add(new ArticleChange(ArticleChange.TYPE.REMOVE, person, article));
	}
}
