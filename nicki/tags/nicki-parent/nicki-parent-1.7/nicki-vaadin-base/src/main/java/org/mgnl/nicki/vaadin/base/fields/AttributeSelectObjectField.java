package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;
import java.util.Iterator;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.data.ReferenceAttributeDataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.AttributeInputListener;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class AttributeSelectObjectField implements DynamicAttributeField, Serializable {

	private Field field;
	private DataContainer property;
	public AttributeSelectObjectField(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {
		if (dynamicObject.getModel().getDynamicAttribute(attributeName).isForeignKey()) {
			Select select = new ComboBox(attributeName);
			select.setContainerDataSource(getOptions(dynamicObject, dynamicObject.getModel().getDynamicAttribute(attributeName)));
			select.setItemCaptionPropertyId("name");
			select.setImmediate(true);
			select.select(dynamicObject.getAttribute(attributeName));
			property = new ReferenceAttributeDataContainer(dynamicObject, attributeName);
			select.setValue(property.getValue());
			select.addListener(new AttributeInputListener(property, objectListener));
			field = select;
		} else {
			field = new TextField(attributeName);
			field.addListener(new AttributeInputListener(property, objectListener));
		}
	}
	
	private Container getOptions(DynamicObject dynamicObject, DynamicAttribute dynamicAttribute) {
		
		Container container = new IndexedContainer();
		container.addContainerProperty("name", String.class, null);
		container.addContainerProperty("dynamicObject", DynamicObject.class, null);
		for (Iterator<DynamicObject> iterator = dynamicAttribute.getOptions(dynamicObject).iterator(); iterator.hasNext();) {
			DynamicObject option = iterator.next();
			Item item = container.addItem(option.getPath());
			item.getItemProperty("dynamicObject").setValue(option);
			item.getItemProperty("name").setValue(option.getDisplayName());
		}
		return container;
	}

	public Component getComponent(boolean readOnly) {
		field.setReadOnly(readOnly);
		return field;
	}
}
