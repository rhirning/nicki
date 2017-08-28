package org.mgnl.nicki.app.menu.navigation;

import java.io.Serializable;

import org.mgnl.nicki.app.menu.application.View;


public class NavigationEntry implements Serializable, NavigationElement {
	private static final long serialVersionUID = 4285844482143266130L;
	private String caption;
	private View view;

	public NavigationEntry(String caption, View view) {
		super();
		this.caption = caption;
		this.view = view;
	}

	public String getCaption() {
		return caption;
	}

	public View getView() {
		return view;
	}

	@Override
	public String getNavigationCaption() {
		return getCaption();
	}

}
