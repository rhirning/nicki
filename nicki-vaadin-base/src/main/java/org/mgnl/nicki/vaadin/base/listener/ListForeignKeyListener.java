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

import java.util.List;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractComponentContainer;

@SuppressWarnings("serial")
public class ListForeignKeyListener extends BaseAttributeListener implements ValueChangeListener {

	private AbstractComponentContainer container;
	public ListForeignKeyListener(AbstractComponentContainer container,
			DynamicObject dynamicObject, String name) {
		super(dynamicObject, name);
		this.container = container;
	}
	public void textChange(TextChangeEvent event) {
	}
	public void valueChange(ValueChangeEvent event) {
		List<Object> values = collectValues(this.container);
		if (values.size() > 0) {
			getDynamicObject().put(getName(), values);
		} else {
			getDynamicObject().remove(getName());
		}
	}
}
