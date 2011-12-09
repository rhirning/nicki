/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class CatalogAttributeInputListener implements ValueChangeListener {
	private InventoryArticle article;
	private CatalogArticleAttribute attribute;

	public CatalogAttributeInputListener(InventoryArticle article,
			CatalogArticleAttribute attribute) {
		this.article = article;
		this.attribute = attribute;
	}

	public void valueChange(ValueChangeEvent event) {
		Object value = event.getProperty().getValue();
		try {
			article.setValue(attribute, value);
		} catch (Exception e) {
			article.setValue(attribute, null);
		}
	}

}
