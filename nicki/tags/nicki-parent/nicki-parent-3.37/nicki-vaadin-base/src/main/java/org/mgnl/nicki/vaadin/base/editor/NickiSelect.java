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

import java.io.Serializable;
import java.util.Collection;

import org.mgnl.nicki.core.objects.DynamicObject;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree.ExpandListener;

public interface NickiSelect extends Serializable {

	void setHeight(String height);

	void setWidth(String width);

	Component getComponent();

	void setImmediate(boolean b);

	void setSelectable(boolean b);

	DynamicObject getValue();

	void addListener(ValueChangeListener listener);

	void addActionHandler(Handler handler);

	void removeItem(Object target);

	void unselect(DynamicObject objectbject);

	void expandItem(DynamicObject object);

	void addListener(ExpandListener listener);

	void setContainerDataSource(Container dataSource);

	void setItemCaptionPropertyId(String propertyName);

	void setItemCaptionMode(ItemCaptionMode property);

	void setItemIconPropertyId(String propertyIcon);

	Collection<?> rootItemIds();

	void expandItemsRecursively(Object id);

	void collapseItemsRecursively(DynamicObject startItemId);


}