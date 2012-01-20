/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
