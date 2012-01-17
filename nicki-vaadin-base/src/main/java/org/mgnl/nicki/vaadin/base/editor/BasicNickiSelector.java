/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	public void setHeight(String height) {
		component.setHeight(height);
	}

	public void setWidth(String width) {
		component.setWidth(width);
	}

	public Component getComponent() {
		return component;
	}

	public void setImmediate(boolean immediate) {
		component.setImmediate(immediate);
	}

	public DynamicObject getValue() {
		return (DynamicObject) component.getValue();
	}

	public void addListener(ValueChangeListener listener) {
		component.addListener(listener);
	}

	public void unselect(DynamicObject object) {
		component.unselect(object);
	}

	public void setItemCaptionPropertyId(String propertyName) {
		component.setItemCaptionPropertyId(propertyName);
	}

	public void setItemCaptionMode(int itemCaptionModeProperty) {
		component.setItemCaptionMode(itemCaptionModeProperty);
	}

	public void setItemIconPropertyId(String propertyIcon) {
		component.setItemIconPropertyId(propertyIcon);
	}

	public void removeItem(Object object) {
		component.removeItem(object);
	}

	public void setContainerDataSource(Container dataSource) {
		component.setContainerDataSource(dataSource);
	}

	protected void setComponent(AbstractSelect component) {
		this.component = component;
	}
	
	public void expandItem(DynamicObject object) {
	}

	public void addListener(ExpandListener listener) {
	}

	public Collection<?> rootItemIds() {
		return null;
	}

	public void expandItemsRecursively(Object id) {
	}

	public void collapseItemsRecursively(DynamicObject startItemId) {
	}



}
