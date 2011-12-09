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

import java.util.Date;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.data.DateHelper;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.PopupDateField;


@SuppressWarnings("serial")
public class DateComponent extends BasicAttributeComponent implements AttributeComponent {

	public Component getInstance(Person user, Person person, InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		setCaption(attribute.getLabel());
		if (isEnabled()) {
			getField().addListener(new CatalogAttributeInputListener(getArticle(), getAttribute()));
		}
		return getField();
	}

	public DateComponent() {
		PopupDateField field = new PopupDateField();
		field.setImmediate(true);
		DateHelper.init(field);
		setField(field);

	}

	@Override
	public String getStringValue(Object value) {
		try {
			Date newValue =(Date) value;
			return DataHelper.getDay(newValue);
		} catch (Exception e) {
			return "";
		}
	}

}
