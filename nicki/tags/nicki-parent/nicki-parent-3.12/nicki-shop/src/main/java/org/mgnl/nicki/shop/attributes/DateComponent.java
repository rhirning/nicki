/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.shop.attributes;

import java.util.Date;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.CatalogArticleAttribute;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.mgnl.nicki.vaadin.base.data.DateHelper;
import org.mgnl.nicki.vaadin.base.fields.SimpleField;

import com.vaadin.ui.Component;
import com.vaadin.ui.PopupDateField;


@SuppressWarnings("serial")
public class DateComponent extends BasicAttributeComponent<Date> implements AttributeComponent<Date> {

	public Component getInstance(Person user, Person person, InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		setCaption(attribute.getLabel());
		try {
			getField().setValue(DataHelper.dateFromString((String) getArticle().getValue(getAttribute())));
		} catch (Exception e) {
		}
		if (isEnabled()) {
			getField().addValueChangeListener(new CatalogAttributeInputListener(getArticle(), getAttribute()));
		}
		return getField().getComponent();
	}

	public DateComponent() {
		PopupDateField field = new PopupDateField();
		field.setImmediate(false);
		DateHelper.init(field);
		setField(new SimpleField<Date>(field));

	}

	@Override
	public String getStringValue(Date value) {
		try {
			return DataHelper.getDay(value);
		} catch (Exception e) {
			return "";
		}
	}


}
