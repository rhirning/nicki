package org.mgnl.nicki.vaadin.base.data;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property;

public interface DataContainer extends Property, Serializable{

	DynamicObject getDynamicObject();
	String getAttributeName();

}
