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
package org.mgnl.nicki.vaadin.base.shop.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

@SuppressWarnings("serial")
public class CategoryChangeListener implements Property.ValueChangeListener{
	private ShopWindow shopWindow;
	private AbstractComponentContainer container;

	public CategoryChangeListener(ShopWindow shopWindow,
			HorizontalLayout container) {
		this.shopWindow = shopWindow;
		this.container = container;
	}

	public void valueChange(ValueChangeEvent event) {
		List<Object> values = collectValues(this.container);
		this.shopWindow.setCategoryFilter(values);
//		this.shopWindow.getWindow().showNotification(values.toString(), Notification.TYPE_WARNING_MESSAGE);

	}
	
	public List<Object> collectValues(AbstractComponentContainer cont) {
		List<Object> list = new ArrayList<Object>();
		for (Iterator<Component> iterator = cont.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component instanceof AbstractField) {
				boolean value = (Boolean) ((AbstractField) component).getValue();
				if (value) {
					list.add(component.getCaption());
				}
			}
			if (component instanceof AbstractComponentContainer) {
				list.addAll(collectValues((AbstractComponentContainer) component));
			}
		}
		return list;
	}

}
