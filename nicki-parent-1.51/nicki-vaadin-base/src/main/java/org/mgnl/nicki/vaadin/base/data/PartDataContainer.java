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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class PartDataContainer extends AttributeDataContainer implements DataContainer, Property {
	private String separator;
	private String name;

	public PartDataContainer(DynamicObject dynamicObject, String name, String attributeName, String separator) {
		super(dynamicObject, attributeName);
		this.separator = separator;
		this.name = name;
	}

	public String getValue() {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) getDynamicObject().get(getAttributeName());
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String value = iterator.next();
			String name = StringUtils.substringBefore(value, this.separator);
			if (StringUtils.equals(name, this.name)) {
				String data = StringUtils.substringAfter(value, this.separator);
				return data;
			}
		}

		return "";
	}

	public void setValue(Object newValue) {
		String value = (String) newValue;
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) getDynamicObject().get(getAttributeName());
		List<String> remover = new ArrayList<String>();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String oldValue = iterator.next();
			String name = StringUtils.substringBefore(oldValue, "=");
			if (StringUtils.equals(name, this.name)) {
				remover.add(oldValue);
			}
		}
		for (Iterator<String> iterator = remover.iterator(); iterator.hasNext();) {
			String toBeRemoved = iterator.next();
			list.remove(toBeRemoved);			
		}
		if (StringUtils.isNotEmpty(value)) {
			list.add(name + separator + value);
		}
	}

}
