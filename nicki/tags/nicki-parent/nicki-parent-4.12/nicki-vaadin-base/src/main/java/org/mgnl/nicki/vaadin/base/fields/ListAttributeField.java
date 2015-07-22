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
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.data.ListAttributeDataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.AddAttributeListener;
import org.mgnl.nicki.vaadin.base.listener.ListAttributeListener;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ListAttributeField extends BaseDynamicAttributeField implements DynamicAttributeField<String>, Serializable {

	private ComponentContainer container;
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener<String> objectListener) {

		@SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) dynamicObject.get(attributeName);
		container = new VerticalLayout();
		VerticalLayout dataLayout = new VerticalLayout();
		DataContainer<List<String>> property = new ListAttributeDataContainer<List<String>>(dynamicObject, attributeName);
		ListAttributeListener listener = new ListAttributeListener(dynamicObject, attributeName, property, dataLayout, objectListener);
		HorizontalLayout hL= new HorizontalLayout();
		Label label = new Label(getName(dynamicObject, attributeName));
		hL.addComponent(label);
		Button newButton =new Button(I18n.getText("nicki.editor.generic.button.add"));
		newButton.addClickListener(new AddAttributeListener(dataLayout, listener));
		hL.addComponent(newButton);
		container.addComponent(hL);
		if (values != null) {
			for (Object valueObject : values) {
				String value = (String) valueObject;
				TextField input = new TextField(null, value);
				input.setImmediate(true);
				input.addValueChangeListener(listener);
				dataLayout.addComponent(input);
			}
		}
		container.addComponent(dataLayout);
	}

	public ComponentContainer getComponent(boolean readOnly) {
		container.setReadOnly(readOnly);
		return container;
	}
}
