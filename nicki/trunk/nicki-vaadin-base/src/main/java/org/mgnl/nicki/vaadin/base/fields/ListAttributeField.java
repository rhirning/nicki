package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.data.ListAttributeDataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.listener.AddAttributeListener;
import org.mgnl.nicki.vaadin.base.listener.ListAttributeListener;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ListAttributeField extends BaseDynamicAttributeField implements DynamicAttributeField, Serializable {

	private ComponentContainer container;
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {

		@SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) dynamicObject.get(attributeName);
		container = new VerticalLayout();
		VerticalLayout dataLayout = new VerticalLayout();
		DataContainer property = new ListAttributeDataContainer(dynamicObject, attributeName);
		ListAttributeListener listener = new ListAttributeListener(dynamicObject, attributeName, property, dataLayout, objectListener);
		HorizontalLayout hL= new HorizontalLayout();
		Label label = new Label(getName(dynamicObject, attributeName));
		hL.addComponent(label);
		Button newButton =new Button(I18n.getText("nicki.editor.generic.button.add"));
		newButton.addListener(new AddAttributeListener(dataLayout, listener));
		hL.addComponent(newButton);
		container.addComponent(hL);
		if (values != null) {
			for (Iterator<Object> iterator = values.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				TextField input = new TextField(null, value);
				input.setImmediate(true);
				input.addListener(listener);
				dataLayout.addComponent(input);
			}
		}
		container.addComponent(dataLayout);
	}

	@Override
	public ComponentContainer getComponent(boolean readOnly) {
		container.setReadOnly(readOnly);
		return container;
	}
}
