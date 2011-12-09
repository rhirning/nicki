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

public interface Command {

	public void execute() throws CommandException;
	
	public String getTitle();

	public String getHeadline();

	public String getCancelCaption();

	public String getConfirmCaption();

	public String getErrorText();
}
