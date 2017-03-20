package org.mgnl.nicki.app.menu.navigation;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NavigationLabel implements Serializable {
	private String caption;

	public NavigationLabel(String caption) {
		super();
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

}
