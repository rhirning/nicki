/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.objects;

import java.util.Enumeration;

public interface ContextAttribute {

	Enumeration<Object> getAll() throws DynamicObjectException;

	Object get() throws DynamicObjectException;

}
