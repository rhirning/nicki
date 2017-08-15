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

import java.util.List;

import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractComponentContainer;

@SuppressWarnings("serial")
public class ListAttributeListener extends BaseAttributeListener implements ValueChangeListener {

	private DataContainer<List<String>> property;
	private AbstractComponentContainer container;
	private DynamicObjectValueChangeListener<String> objectListener;
	
	private ListAttributeListener(AbstractComponentContainer container,
			DynamicObject dynamicObject, String name) {
		super(dynamicObject, name);
		this.container = container;
	}
	public void textChange(TextChangeEvent event) {
	}
	public void valueChange(ValueChangeEvent event) {
		List<String> values = collectValues(this.container);
		property.setValue(values);
		if (values.size() > 0) {
			getDynamicObject().put(getName(), values);
		} else {
			getDynamicObject().remove(getName());
		}
		if (objectListener != null) {
			objectListener.valueChange(property.getDynamicObject(), getName(), values);
		}
		property.getDynamicObject().setModified(true);

		
		
	}
	public ListAttributeListener(DynamicObject dynamicObject, String attributeName,
			DataContainer<List<String>> property, AbstractComponentContainer container, DynamicObjectValueChangeListener<String> objectListener) {
		super(dynamicObject, attributeName);
		this.property = property;
		this.container = container;
		this.objectListener = objectListener;
	}
}
