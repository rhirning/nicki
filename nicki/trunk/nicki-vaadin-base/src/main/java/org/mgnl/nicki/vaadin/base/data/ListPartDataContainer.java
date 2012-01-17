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
