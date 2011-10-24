package org.mgnl.nicki.vaadin.base.shop.core;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

import com.vaadin.data.util.IndexedContainer;

public interface ShopContainer extends Serializable {
	static final String PROPERTY_NAME = "name"; 
	static final String PROPERTY_CATEGORY = "category"; 
	static final String PROPERTY_STATUS = "status"; 
	static final String PROPERTY_ICON = "icon"; 
	static final String PROPERTY_PATH= "path"; 
		
	IndexedContainer getArticles() throws DynamicObjectException;

	void orderItem(Object target);
	
	void cancelItem(Object target);

	void setCategoryFilter(List<Object> values);

	Person getPerson();

	String[] getVisibleColumns();
}
