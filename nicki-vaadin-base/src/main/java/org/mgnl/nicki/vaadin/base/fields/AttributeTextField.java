/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.AttributeDataContainer;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.AttributeInputListener;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class AttributeTextField  extends BaseDynamicAttributeField implements DynamicAttributeField, Serializable {

	private AbstractField field;
	private DataContainer property;
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {

		property = new AttributeDataContainer(dynamicObject, attributeName);
		field = new TextField(getName(dynamicObject, attributeName));
		field.setHeight(2, Sizeable.UNITS_EM);
		field.setWidth("400px");
		field.setValue(property.getValue());
		field.setImmediate(false);
		field.addListener(new AttributeInputListener(property, objectListener));
	}

	public Field getComponent(boolean readOnly) {
		field.setReadOnly(readOnly);
		return field;
	}
	
}
