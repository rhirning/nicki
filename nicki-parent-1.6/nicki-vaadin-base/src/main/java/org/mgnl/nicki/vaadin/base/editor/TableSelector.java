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

	@Override
	public void setSelectable(boolean selectable) {
		component.setSelectable(selectable);
	}

	@Override
	public void addActionHandler(Handler handler) {
		component.addActionHandler(handler);
	}

}
