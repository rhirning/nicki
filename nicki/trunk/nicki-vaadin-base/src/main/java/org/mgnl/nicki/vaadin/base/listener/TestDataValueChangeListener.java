/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Field;

@SuppressWarnings("serial")
public class TestDataValueChangeListener implements ValueChangeListener {

	private Property dataContainer;
	private ComponentContainer testData;
	private String separator;
	public TestDataValueChangeListener(Property dataContainer, ComponentContainer testData, String separator) {
		this.dataContainer = dataContainer;
		this.testData = testData;
		this.separator = separator;
	}

	public void valueChange(ValueChangeEvent event) {
		dataContainer.setValue(collectValues(testData));
	}

	public List<String> collectValues(ComponentContainer cont) {
		List<String> list = new ArrayList<String>();
		for (Iterator<Component> iterator = cont.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component instanceof Field) {
				String caption = component.getCaption();
				String value = (String) ((Field) component).getValue();
				list.add(caption + separator + value);
			}
			if (component instanceof ComponentContainer) {
				list.addAll(collectValues((AbstractComponentContainer) component));
			}
		}
		return list;
	}

	
}
