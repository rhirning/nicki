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

import java.io.Serializable;
import java.util.Collection;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action.Handler;
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

	void setItemCaptionMode(int itemCaptionModeProperty);

	void setItemIconPropertyId(String propertyIcon);

	Collection<?> rootItemIds();

	void expandItemsRecursively(Object id);

	void collapseItemsRecursively(DynamicObject startItemId);


}
