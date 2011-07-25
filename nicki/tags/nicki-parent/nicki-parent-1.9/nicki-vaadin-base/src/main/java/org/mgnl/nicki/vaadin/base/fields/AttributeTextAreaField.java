package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.AttributeDataContainer;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.AttributeInputListener;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

@SuppressWarnings("serial")
public class AttributeTextAreaField implements DynamicAttributeField, Serializable {

	private AbstractField field;
	private DataContainer property;
	public AttributeTextAreaField(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {
		property = new AttributeDataContainer(dynamicObject, attributeName);
		field = new TextArea(attributeName);
		field.setWidth("400px");
		field.setHeight("-1px");
		field.setValue(property.getValue());
		field.setImmediate(false);
		field.addListener(new AttributeInputListener(property, objectListener));
	}

	public Field getComponent(boolean readOnly) {
		field.setReadOnly(readOnly);
		return field;
	}
	
}
