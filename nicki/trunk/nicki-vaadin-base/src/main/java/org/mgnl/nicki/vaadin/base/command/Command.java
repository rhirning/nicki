package org.mgnl.nicki.vaadin.base.command;

public interface Command {

	public void execute() throws CommandException;

	public Object getHeadline();

	public String getCancelCaption();

	public String getConfirmCaption();

	public String getErrorText();
}
