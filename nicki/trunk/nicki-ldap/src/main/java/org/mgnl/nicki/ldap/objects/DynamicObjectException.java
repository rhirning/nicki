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

@SuppressWarnings("serial")
public class DynamicObjectException extends Exception {

	public DynamicObjectException(String message) {
		super(message);
	}

	public DynamicObjectException(Exception e) {
		super(e);
	}

}
