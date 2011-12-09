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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class ListPartDataContainer extends AttributeDataContainer implements DataContainer, Property {
	private String separator;

	public ListPartDataContainer(DynamicObject dynamicObject, String attributeName, String separator) {
		super(dynamicObject, attributeName);
		this.separator = separator;
	}

	public Map<String, String> getValue() {
		Map<String, String> values = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) getDynamicObject().get(getAttributeName());
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String value = iterator.next();
			String name = StringUtils.substringBefore(value, this.separator);
			String data = StringUtils.substringAfter(value, this.separator);
			values.put(name, data);
		}

		return values;
	}

	public void setValue(Map<String, String> values) {
		List<String> listvalues = new ArrayList<String>();
		for (Iterator<String> iterator = values.keySet().iterator(); iterator.hasNext();) {
			String  name = iterator.next();
			listvalues.add(name + this.separator + values.get(name));
		}
		getDynamicObject().put(getAttributeName(), listvalues);
	}

}
