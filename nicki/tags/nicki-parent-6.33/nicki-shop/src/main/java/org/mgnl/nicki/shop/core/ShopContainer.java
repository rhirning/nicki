/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.core;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;

import com.vaadin.data.util.IndexedContainer;

public interface ShopContainer extends Serializable {
	String PROPERTY_NAME = "name"; 
	String PROPERTY_CATEGORY = "category"; 
	String PROPERTY_STATUS = "status"; 
	String PROPERTY_ICON = "icon"; 
	String PROPERTY_PATH= "path"; 
		
	IndexedContainer getArticles() throws DynamicObjectException;

	void orderItem(Object target);
	
	void cancelItem(Object target);

	void setCategoryFilter(List<Object> values);

	Person getPerson();

	String[] getVisibleColumns();
}
