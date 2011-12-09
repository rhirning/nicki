/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.data;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property;

public interface DataContainer extends Property, Serializable{

	DynamicObject getDynamicObject();
	String getAttributeName();

}
