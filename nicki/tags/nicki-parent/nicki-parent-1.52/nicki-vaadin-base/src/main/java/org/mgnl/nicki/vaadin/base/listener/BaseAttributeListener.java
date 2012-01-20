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
package org.mgnl.nicki.vaadin.base.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public abstract class BaseAttributeListener implements ValueChangeListener {

	private DynamicObject dynamicObject;
	private String name;
	public BaseAttributeListener(DynamicObject dynamicObject, String name) {
		this.setDynamicObject(dynamicObject);
		this.setName(name);
	}
	public abstract void textChange(TextChangeEvent event);

	public List<Object> collectValues(AbstractComponentContainer cont) {
		List<Object> list = new ArrayList<Object>();
		for (Iterator<Component> iterator = cont.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component instanceof AbstractField) {
				String value = (String) ((AbstractField) component).getValue();
				if (StringUtils.isNotEmpty(value)) {
					list.add(value);
				}
			}
			if (component instanceof AbstractComponentContainer) {
				list.addAll(collectValues((AbstractComponentContainer) component));
			}
		}
		return list;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setDynamicObject(DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
	}
	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

}
