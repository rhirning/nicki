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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;

@SuppressWarnings("serial")
public class ForeignKeyInputListener extends BaseAttributeListener implements ValueChangeListener {

	public ForeignKeyInputListener(DynamicObject dynamicObject, String name) {
		super(dynamicObject, name);
	}

	public void textChange(TextChangeEvent event) {
	}

	public void valueChange(ValueChangeEvent event) {
		String value = (String) event.getProperty().getValue();
		if (StringUtils.isNotEmpty(value)) {
			getDynamicObject().put(getName(), value);
		} else {
			getDynamicObject().remove(getName());
		}
	}

}
