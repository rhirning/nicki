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

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.TextArea;

@SuppressWarnings("serial")
public class AttributeTextAreaField extends BaseDynamicAttributeField implements DynamicAttributeField, Serializable {

	private AbsoluteLayout mainLayout;

	private TextArea field;
	private DataContainer property;
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		mainLayout.setWidth("500px");
		mainLayout.setHeight("120px");

		property = new AttributeDataContainer(dynamicObject, attributeName);
		field = new TextArea(getName(dynamicObject, attributeName));
		field.setWidth("400px");
		field.setHeight("100px");
		field.setValue(property.getValue());
		field.setImmediate(false);
		field.addListener(new AttributeInputListener(property, objectListener));
		mainLayout.addComponent(field, "top:20.0px;left:0.0px;");
		
		// editButton
		Button editButton = new Button();
		editButton.setWidth("-1px");
		editButton.setHeight("-1px");
		editButton.setCaption("Edit");
		editButton.setImmediate(false);
//		mainLayout.addComponent(editButton, "top:20.0px;left:620.0px;");

		
	}

	public ComponentContainer getComponent(boolean readOnly) {
		field.setReadOnly(readOnly);
		return mainLayout;
	}
	
}
