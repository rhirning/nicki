/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.data;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class ReferenceAttributeDataContainer implements DataContainer, Property {

	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

	public String getAttributeName() {
		return attributeName;
	}

	private DynamicObject dynamicObject;
	private String attributeName;
	private boolean readOnly = false;
	
	public ReferenceAttributeDataContainer(DynamicObject dynamicObject, String attributeName) {
		this.dynamicObject = dynamicObject;
		this.attributeName = attributeName;
	}

	public Object getValue() {
		return StringUtils.trimToEmpty((String) dynamicObject.get(attributeName));
	}

	public void setValue(Object newValue) throws ReadOnlyException,
			ConversionException {
		dynamicObject.put(attributeName, (String) newValue);
	}

	public Class<?> getType() {
		return dynamicObject.getModel().getDynamicAttribute(attributeName).getClass();
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean newStatus) {
		this.readOnly = newStatus;
	}

}
