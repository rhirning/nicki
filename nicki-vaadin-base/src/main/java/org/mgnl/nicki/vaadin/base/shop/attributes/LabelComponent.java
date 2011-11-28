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
package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class LabelComponent extends BasicAttributeComponent implements AttributeComponent {

	private Label label;

	public LabelComponent() {
		label = new Label();
	}

	@Override
	public Component getInstance(Person user, Person person, InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		String value = attribute.getLabel();
		if (StringUtils.isNotEmpty(value)) {
			value += ": ";
		}
		String content = getContent(String.class, user, person);
		value += StringUtils.trimToEmpty(content);
		label.setCaption(value);
		return label;
	}

	public Component XXXgetInstance(Person user, Person person, InventoryArticle article,
			CatalogArticleAttribute attribute) {
//		this.article = article;
//		this.attribute = attribute;
		setCaption(attribute.getLabel());
		return label;
	}

	@Override
	public void setValue(Object value) {
	}

	@Override
	public Object getvalue() {
		return null;
	}
	@Override
	public void setCaption(String caption) {
		label.setCaption(caption);
	}
	@Override
	public void setEnabled(boolean enabled) {
	}
	@Override
	public boolean isEnabled() {
		return false;
	}
	@Override
	public String getStringValue(Object value) {
		return (String) value;
	}
}
