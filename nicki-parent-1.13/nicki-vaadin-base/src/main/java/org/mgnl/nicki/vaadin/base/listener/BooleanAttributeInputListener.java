package org.mgnl.nicki.vaadin.base.listener;

import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class BooleanAttributeInputListener implements ValueChangeListener {

	DataContainer property = null;
	private DynamicObjectValueChangeListener objectListener = null;

	public BooleanAttributeInputListener(DataContainer property, DynamicObjectValueChangeListener objectListener) {
		this.property = property;
		this.objectListener = objectListener;
	}

	public void valueChange(ValueChangeEvent event) {
		String value = event.getProperty().getValue().toString();
		property.setValue(value);
		if (objectListener != null) {
			objectListener.valueChange(property.getDynamicObject(), property.getAttributeName(), value);
		}
	}

}
