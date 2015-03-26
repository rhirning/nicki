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

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.CatalogArticleAttribute;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class GenericComponent extends BasicAttributeComponent<String> implements AttributeComponent<String> {
	
	private VaadinComponent vaadinComponent = null;

	public GenericComponent() {
	}
	public Component getInstance(Person user, Person person, InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		vaadinComponent = getVaadinContent();
		setCaption(attribute.getLabel());
		vaadinComponent.setValue((String) getArticle().getValue(getAttribute()));

		vaadinComponent.addValueChangeListener(new CatalogAttributeInputListener(getArticle(), getAttribute()));
	
		return vaadinComponent.getComponent();
	}

	@Override
	public void setValue(String value) {
		vaadinComponent.setValue(value);
	}

	@Override
	public String getValue() {
		return vaadinComponent.getValue();
	}
	@Override
	public void setCaption(String caption) {
		vaadinComponent.setCaption(caption);
	}
	@Override
	public void setEnabled(boolean enabled) {
	}
	@Override
	public boolean isEnabled() {
		return vaadinComponent.isEnabled();
	}
	@Override
	public String getStringValue(String value) {
		return (String) value;
	}
	
	
	
}
