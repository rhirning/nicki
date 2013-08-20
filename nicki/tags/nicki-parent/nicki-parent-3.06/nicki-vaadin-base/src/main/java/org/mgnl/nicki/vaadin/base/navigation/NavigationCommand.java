package org.mgnl.nicki.vaadin.base.navigation;

import java.util.ArrayList;
import java.util.List;

public class NavigationCommand {
	private List<NavigationCommandEntry> entries = new ArrayList<NavigationCommandEntry>();

	public void add(String commandName, Object... data) {
		entries.add(new NavigationCommandEntry(commandName, data));
	}

	public List<NavigationCommandEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<NavigationCommandEntry> entries) {
		this.entries = entries;
	}

	public void remove(NavigationCommandEntry navigationCommandEntry) {
		entries.remove(navigationCommandEntry);
	}
	
	public boolean hasCommands() {
		return entries.size() > 0;
	}

}
