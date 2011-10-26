package org.mgnl.nicki.vaadin.base.command;

import org.mgnl.nicki.ldap.objects.DynamicObjectException;

@SuppressWarnings("serial")
public class CommandException extends Exception {

	public CommandException(DynamicObjectException e) {
		super(e);
	}

}
