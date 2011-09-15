package org.mgnl.nicki.shop.rules;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticle;

public class ArticleChange {
	public enum TYPE {ADD, REMOVE};
	
	private TYPE type;
	private Person person;
	private CatalogArticle article;
	public ArticleChange(TYPE type, Person person, CatalogArticle article) {
		super();
		this.type = type;
		this.person = person;
		this.article = article;
	}
	public TYPE getType() {
		return type;
	}
	public Person getPerson() {
		return person;
	}
	public CatalogArticle getArticle() {
		return article;
	}

}
