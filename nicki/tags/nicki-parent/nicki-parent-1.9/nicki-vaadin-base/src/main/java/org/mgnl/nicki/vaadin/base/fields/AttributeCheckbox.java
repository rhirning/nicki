package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.AttributeDataContainer;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.BooleanAttributeInputListener;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;

@SuppressWarnings("serial")
public class AttributeCheckbox implements DynamicAttributeField, Serializable {

	private AbstractField field;
	private DataContainer property;
	public AttributeCheckbox(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {
		property = new AttributeDataContainer(dynamicObject, attributeName);
		field = new CheckBox(attributeName);
		field.setHeight(1.5f, Sizeable.UNITS_EM);
		field.setValue(DataHelper.booleanOf((String) property.getValue()));
		field.setImmediate(false);
		field.addListener(new BooleanAttributeInputListener(property, objectListener));
	}

	public Field getComponent(boolean readOnly) {
		field.setReadOnly(readOnly);
		return field;
	}
	
}
