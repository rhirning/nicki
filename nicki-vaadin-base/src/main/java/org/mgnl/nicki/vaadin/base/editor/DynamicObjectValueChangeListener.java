/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.editor;

import java.util.List;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.ui.Component;

public interface DynamicObjectValueChangeListener {

	void valueChange(DynamicObject dynamicObject, String name, List<Object> values);

	void valueChange(DynamicObject dynamicObject, String attributeName, String value);

	boolean acceptAttribute(String name);

	void close(Component component);

	void refresh(DynamicObject dynamicObject);

}
