package org.mgnl.nicki.shop.rules;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.Selector;

public class BasicValueProvider {
	private Selector selector;
	private String i18nBase;

	public void init(Selector selector, String i18nBase) {
		this.setSelector(selector);
		setI18nBase(i18nBase);
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public Selector getSelector() {
		return selector;
	}

	public static String getLdapName(CatalogArticle article, String selectorName) {
		try {
			Person person = article.getContext().getObjectFactory().getDynamicObject(Person.class);
			return person.getModel().getAttributes().get(selectorName).getLdapName();
		} catch (InstantiateDynamicObjectException e) {
			e.printStackTrace();
			return "INVALID";
		}
	}

	public void setI18nBase(String i18nBase) {
		this.i18nBase = i18nBase;
	}

	public String getI18nBase() {
		return i18nBase;
	}

}
