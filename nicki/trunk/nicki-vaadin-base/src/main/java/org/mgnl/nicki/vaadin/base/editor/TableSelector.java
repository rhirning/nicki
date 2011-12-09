/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.editor;

import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableSelector extends BasicNickiSelector {
	private Table component = new Table();

	public TableSelector() {
		super();
		super.setComponent(component);
	}

	public void setSelectable(boolean selectable) {
		component.setSelectable(selectable);
	}

	public void addActionHandler(Handler handler) {
		component.addActionHandler(handler);
	}

}
