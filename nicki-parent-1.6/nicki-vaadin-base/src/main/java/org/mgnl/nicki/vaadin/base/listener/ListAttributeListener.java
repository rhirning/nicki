package org.mgnl.nicki.vaadin.base.listener;

import java.util.List;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractComponentContainer;

@SuppressWarnings("serial")
public class ListAttributeListener extends BaseAttributeListener implements ValueChangeListener {

	private DataContainer property;
	private AbstractComponentContainer container;
	private DynamicObjectValueChangeListener objectListener;
	
	private ListAttributeListener(AbstractComponentContainer container,
			DynamicObject dynamicObject, String name) {
		super(dynamicObject, name);
		this.container = container;
	}
	public void textChange(TextChangeEvent event) {
	}
	public void valueChange(ValueChangeEvent event) {
		List<Object> values = collectValues(this.container);
		property.setValue(values);
		if (values.size() > 0) {
			getDynamicObject().put(getName(), values);
		} else {
			getDynamicObject().remove(getName());
		}
		if (objectListener != null) {
			objectListener.valueChange(property.getDynamicObject(), getName(), values);
		}
		
	}
	public ListAttributeListener(DynamicObject dynamicObject, String attributeName,
			DataContainer property, AbstractComponentContainer container, DynamicObjectValueChangeListener objectListener) {
		super(dynamicObject, attributeName);
		this.property = property;
		this.container = container;
		this.objectListener = objectListener;
	}
}
