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

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.AttributeDataContainer;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.BooleanAttributeInputListener;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;

@SuppressWarnings("serial")
public class AttributeCheckbox extends BaseDynamicAttributeField implements DynamicAttributeField<String>, Serializable {

	private CheckBox field;
	private DataContainer<String> property;
	public void init (String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener<String> objectListener) {

		property = new AttributeDataContainer<String>(dynamicObject, attributeName);
		field = new CheckBox(getName(dynamicObject, attributeName));
		field.setHeight(1.5f, Unit.EM);
		field.setValue(DataHelper.booleanOf(property.getValue()));
		field.setImmediate(false);
		field.addValueChangeListener(new BooleanAttributeInputListener(property, objectListener));
	}

	public Field<Boolean> getComponent(boolean readOnly) {
		field.setReadOnly(readOnly);
		return field;
	}
	
}
