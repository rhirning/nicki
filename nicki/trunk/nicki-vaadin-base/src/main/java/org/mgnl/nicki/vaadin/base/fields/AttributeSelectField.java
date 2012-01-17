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
