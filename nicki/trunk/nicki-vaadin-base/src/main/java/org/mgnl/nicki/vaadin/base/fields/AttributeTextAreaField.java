/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
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
