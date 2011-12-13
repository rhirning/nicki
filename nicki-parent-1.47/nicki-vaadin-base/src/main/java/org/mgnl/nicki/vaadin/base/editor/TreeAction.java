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

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.ui.Window;

public interface TreeAction {
	public void execute(Window parentWindow, DynamicObject dynamicObject);

	public String getName();

	public void close();
	
	public Class<? extends DynamicObject> getTargetClass();
}
