/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.components;

import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

import com.vaadin.ui.ComponentContainer;

public interface NewClassEditor extends ComponentContainer{

	void init(DynamicObject parent, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException;

}
