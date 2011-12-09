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

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;

@SuppressWarnings("serial")
public class CostCenterComponent extends BasicAttributeComponent implements AttributeComponent {

	public CostCenterComponent() {
		NativeSelect field = new NativeSelect();
		field.addItem("12345");
		field.setImmediate(true);
		setField(field);
	}
	public Component getInstance(Person user, Person person, InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		setCaption(attribute.getLabel());
		if (isEnabled()) {
			getField().addListener(new CatalogAttributeInputListener(getArticle(), getAttribute()));
		}
		return getField();
	}
	
}
