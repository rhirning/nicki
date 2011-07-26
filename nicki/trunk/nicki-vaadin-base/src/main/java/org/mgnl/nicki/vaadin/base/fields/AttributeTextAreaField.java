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
public class AttributeTextAreaField implements DynamicAttributeField, Serializable {

	private AbsoluteLayout mainLayout;

	private TextArea field;
	private DataContainer property;
	public AttributeTextAreaField(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		mainLayout.setWidth("700px");
		mainLayout.setHeight("240px");

		property = new AttributeDataContainer(dynamicObject, attributeName);
		field = new TextArea(attributeName);
		field.setWidth("600px");
		field.setHeight("200px");
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
