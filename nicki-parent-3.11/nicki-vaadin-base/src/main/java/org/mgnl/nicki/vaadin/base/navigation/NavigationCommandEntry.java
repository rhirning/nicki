package org.mgnl.nicki.vaadin.base.navigation;

public class NavigationCommandEntry {
	private	String commandName = null;
	private Object[] data = null;
	
	public NavigationCommandEntry(String commandName, Object[] data) {
		super();
		this.setCommandName(commandName);
		this.setData(data);
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public Object[] getData() {
		return data;
	}

	public void setData(Object[] data) {
		this.data = data;
	}


}
