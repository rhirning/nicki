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
package org.mgnl.nicki.vaadin.base.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.DynamicObject;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public abstract class BaseAttributeListener implements ValueChangeListener {

	private DynamicObject dynamicObject;
	private String name;
	public BaseAttributeListener(DynamicObject dynamicObject, String name) {
		this.setDynamicObject(dynamicObject);
		this.setName(name);
	}
	public abstract void textChange(TextChangeEvent event);

	public List<String> collectValues(AbstractComponentContainer cont) {
		List<String> list = new ArrayList<String>();
		for (Component component : cont) {
			if (component instanceof AbstractField) {
				@SuppressWarnings("unchecked")
				String value = ((AbstractField<String>) component).getValue();
				if (StringUtils.isNotEmpty(value)) {
					list.add(value);
				}
			}
			if (component instanceof AbstractComponentContainer) {
				list.addAll(collectValues((AbstractComponentContainer) component));
			}
		}
		return list;
	}
	private void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	private void setDynamicObject(DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
	}
	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

}
