package org.mgnl.nicki.shop.selector;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleSelector;

public abstract class BasicSelector implements CatalogArticleSelector {
	private Person shopper;
	private Person recipient;

	public Person getShopper() {
		return shopper;
	}


	public void setShopper(Person shopper) {
		this.shopper = shopper;
	}


	public Person getRecipient() {
		return recipient;
	}


	public void setRecipient(Person recipient) {
		this.recipient = recipient;
	}

}
