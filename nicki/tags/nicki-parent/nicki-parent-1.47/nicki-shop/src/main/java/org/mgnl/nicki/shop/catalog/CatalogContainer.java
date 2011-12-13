/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.shop.catalog;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public interface CatalogContainer extends Serializable {
	static final String PROPERTY_NAME = "name"; 
	static final String PROPERTY_CATEGORY = "category"; 
	static final String PROPERTY_STATUS = "status"; 
	static final String PROPERTY_ICON = "icon"; 
	static final String PROPERTY_PATH= "path"; 
		
	List<CatalogArticle> getArticles() throws DynamicObjectException;

	void orderItem(Object target);
	
	void cancelItem(Object target);

	void setCategoryFilter(List<Object> values);

	Person getPerson();

	String[] getVisibleColumns();
}
