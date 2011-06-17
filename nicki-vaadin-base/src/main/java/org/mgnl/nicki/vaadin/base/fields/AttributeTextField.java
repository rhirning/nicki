package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.AttributeDataContainer;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.AttributeInputListener;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class AttributeTextField implements Serializable {

	private AbstractField field;
	private DataContainer property;
	public AttributeTextField(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {
		property = new AttributeDataContainer(dynamicObject, attributeName);
		field = new TextField(attributeName);
		field.setValue(property.getValue());
		field.setImmediate(false);
		field.addListener(new AttributeInputListener(property, objectListener));
	}

	public Field getField() {
		return field;
	}

	public Field getField(boolean readOnly) {
		field.setReadOnly(readOnly);
		return field;
	}
	
}
