package org.mgnl.nicki.vaadin.base.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class NavigationFolder implements Serializable, NavigationElement {
	private NavigationLabel label;
	private boolean separator = false;
	
	private List<NavigationEntry> entries = new ArrayList<NavigationEntry>();


	public List<NavigationEntry> getEntries() {
		return entries;
	}

	public NavigationFolder() {
		super();
		this.separator = true;
	}

	public NavigationFolder(NavigationLabel label) {
		super();
		this.label = label;
	}
	
	public void addEntry(NavigationEntry entry) {
		entries.add(entry);
	}

	public NavigationLabel getLabel() {
		return label;
	}

	public boolean isSeparator() {
		return separator;
	}

	@Override
	public String getNavigationCaption() {
		return label.getCaption();
	}

}
