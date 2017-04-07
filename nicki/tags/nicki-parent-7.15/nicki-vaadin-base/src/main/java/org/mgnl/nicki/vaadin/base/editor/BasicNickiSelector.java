/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.editor;

import java.util.Collection;

import org.mgnl.nicki.core.data.TreeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Tree.ExpandListener;

@SuppressWarnings("serial")
public abstract class BasicNickiSelector implements NickiSelect {
	private static final Logger LOG = LoggerFactory.getLogger(BasicNickiSelector.class);
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

	public TreeData getValue() {
		return (TreeData) component.getValue();
	}

	public void addListener(ValueChangeListener listener) {
		component.addValueChangeListener(listener);
	}

	public void unselect(TreeData object) {
		component.unselect(object);
	}

	public void setItemCaptionPropertyId(String propertyName) {
		component.setItemCaptionPropertyId(propertyName);
	}

	public void setItemCaptionMode(ItemCaptionMode itemCaptionMode) {
		component.setItemCaptionMode(itemCaptionMode);
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
	
	@Override
	public void expandItem(TreeData object) {
		LOG.debug("not implemented");
	}

	@Override
	public void addListener(ExpandListener listener) {
		LOG.debug("not implemented");
	}

	@Override
	public Collection<?> rootItemIds() {
		LOG.debug("not implemented");
		return null;
	}

	@Override
	public void expandItemsRecursively(Object id) {
		LOG.debug("not implemented");
	}

	@Override
	public void collapseItemsRecursively(TreeData startItemId) {
		LOG.debug("not implemented");
	}



}
