package org.mgnl.nicki.vaadin.base.editor;

import java.util.Collection;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree.ExpandListener;

@SuppressWarnings("serial")
public abstract class BasicNickiSelector implements NickiSelect {
	private AbstractSelect component;

	@Override
	public void setHeight(String height) {
		component.setHeight(height);
	}

	@Override
	public void setWidth(String width) {
		component.setWidth(width);
	}

	@Override
	public Component getComponent() {
		return component;
	}

	@Override
	public void setImmediate(boolean immediate) {
		component.setImmediate(immediate);
	}

	@Override
	public DynamicObject getValue() {
		return (DynamicObject) component.getValue();
	}

	@Override
	public void addListener(ValueChangeListener listener) {
		component.addListener(listener);
	}

	@Override
	public void unselect(DynamicObject object) {
		component.unselect(object);
	}

	@Override
	public void setItemCaptionPropertyId(String propertyName) {
		component.setItemCaptionPropertyId(propertyName);
	}

	@Override
	public void setItemCaptionMode(int itemCaptionModeProperty) {
		component.setItemCaptionMode(itemCaptionModeProperty);
	}

	@Override
	public void setItemIconPropertyId(String propertyIcon) {
		component.setItemIconPropertyId(propertyIcon);
	}

	@Override
	public void removeItem(Object object) {
		component.removeItem(object);
	}

	@Override
	public void setContainerDataSource(Container dataSource) {
		component.setContainerDataSource(dataSource);
	}

	protected void setComponent(AbstractSelect component) {
		this.component = component;
	}
	
	@Override
	public void expandItem(DynamicObject object) {
	}

	@Override
	public void addListener(ExpandListener listener) {
	}

	@Override
	public Collection<?> rootItemIds() {
		return null;
	}

	@Override
	public void expandItemsRecursively(Object id) {
	}

	@Override
	public void collapseItemsRecursively(DynamicObject startItemId) {
	}



}
