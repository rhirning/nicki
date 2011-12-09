/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.data;

@SuppressWarnings("serial")
public class InstantiateDynamicObjectException extends Exception {

	public InstantiateDynamicObjectException(Exception e) {
		super(e);
	}

	public InstantiateDynamicObjectException(String text) {
		super(text);
	}

}
