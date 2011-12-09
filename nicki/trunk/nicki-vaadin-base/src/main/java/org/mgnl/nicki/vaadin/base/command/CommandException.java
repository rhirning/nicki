/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.command;

import org.mgnl.nicki.ldap.objects.DynamicObjectException;

@SuppressWarnings("serial")
public class CommandException extends Exception {

	public CommandException(DynamicObjectException e) {
		super(e);
	}

}
