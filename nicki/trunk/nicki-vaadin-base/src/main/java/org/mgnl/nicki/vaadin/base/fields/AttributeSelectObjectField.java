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
package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;
import java.util.Iterator;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.data.ReferenceAttributeDataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.AttributeInputListener;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class AttributeSelectObjectField extends BaseDynamicAttributeField implements DynamicAttributeField, Serializable {

	private Field field;
	private DataContainer property;
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {

		if (dynamicObject.getModel().getDynamicAttribute(attributeName).isForeignKey()) {
			Select select = new ComboBox(getName(dynamicObject, attributeName));
			select.setContainerDataSource(getOptions(dynamicObject, dynamicObject.getModel().getDynamicAttribute(attributeName)));
			select.setItemCaptionPropertyId("name");
			select.setImmediate(true);
			select.select(dynamicObject.getAttribute(attributeName));
			property = new ReferenceAttributeDataContainer(dynamicObject, attributeName);
			select.setValue(property.getValue());
			select.addListener(new AttributeInputListener(property, objectListener));
			field = select;
		} else {
			field = new TextField(attributeName);
			field.addListener(new AttributeInputListener(property, objectListener));
		}
	}
	
	private Container getOptions(DynamicObject dynamicObject, DynamicAttribute dynamicAttribute) {
		
		Container container = new IndexedContainer();
		container.addContainerProperty("name", String.class, null);
		container.addContainerProperty("dynamicObject", DynamicObject.class, null);
		for (Iterator<? extends DynamicObject> iterator = dynamicAttribute.getOptions(dynamicObject).iterator(); iterator.hasNext();) {
			DynamicObject option = iterator.next();
			Item item = container.addItem(option.getPath());
			item.getItemProperty("dynamicObject").setValue(option);
			item.getItemProperty("name").setValue(option.getDisplayName());
		}
		return container;
	}

	public Component getComponent(boolean readOnly) {
		field.setReadOnly(readOnly);
		return field;
	}
}
