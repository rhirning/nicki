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

import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.AttributeDataContainer;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.AttributeInputListener;

import com.vaadin.data.Container;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;

@SuppressWarnings("serial")
public class AttributeSelectField extends BaseDynamicAttributeField implements DynamicAttributeField, Serializable {

	private Field field;
	private DataContainer property;
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {

		Select select = new ComboBox(getName(dynamicObject, attributeName));
//		select.setItemCaptionPropertyId("name");
		select.setImmediate(true);
		select.select(dynamicObject.getAttribute(attributeName));
		property = new AttributeDataContainer(dynamicObject, attributeName);
		select.setValue(property.getValue());
		select.addListener(new AttributeInputListener(property, objectListener));
		field = select;
	}
	
	public Component getComponent(boolean readOnly) {
		field.setReadOnly(readOnly);
		return field;
	}

	public void setOptions(Container options) {
		((AbstractSelect) field).setContainerDataSource(options);
		field.setValue(property.getValue());
	}

}
