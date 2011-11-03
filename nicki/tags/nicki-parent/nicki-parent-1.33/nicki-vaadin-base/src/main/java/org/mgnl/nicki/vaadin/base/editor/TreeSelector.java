package org.mgnl.nicki.vaadin.base.editor;

import java.util.Collection;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandListener;

@SuppressWarnings("serial")
public class TreeSelector extends BasicNickiSelector implements NickiSelect {
	private Tree component = new Tree();

	public TreeSelector() {
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


	@Override
	public void expandItem(DynamicObject object) {
		component.expandItem(object);
	}

	@Override
	public void addListener(ExpandListener listener) {
		component.addListener(listener);
	}

	@Override
	public Collection<?> rootItemIds() {
		return component.rootItemIds();
	}

	@Override
	public void collapseItemsRecursively(DynamicObject startItemId) {
		component.collapseItemsRecursively(startItemId);
	}

	@Override
	public void expandItemsRecursively(Object object) {
		component.expandItem(object);
	}

}
