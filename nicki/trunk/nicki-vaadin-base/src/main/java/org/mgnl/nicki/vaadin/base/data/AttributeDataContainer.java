/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.vaadin.base.data;

import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class AttributeDataContainer<T> implements DataContainer<T>, Property<T> {
	private static final Logger LOG = LoggerFactory.getLogger(AttributeDataContainer.class);

	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

	public String getAttributeName() {
		return attributeName;
	}

	private DynamicObject dynamicObject;
	private String attributeName;
	private boolean readOnly = false;
	
	public AttributeDataContainer(DynamicObject dynamicObject, String attributeName) {
		this.dynamicObject = dynamicObject;
		this.attributeName = attributeName;
	}

	@SuppressWarnings("unchecked")
	public T getValue() {
		if (dynamicObject.get(attributeName) == null) {
			try {
				if (String.class == getType()) {
					return (T) "";
				}
			} catch (Exception e) {
				LOG.debug("Error", e);
				// nothing to do
			}
		}
		return (T) dynamicObject.get(attributeName);
	}

	public void setValue(T newValue) {
		dynamicObject.put(attributeName, newValue);
	}

	@SuppressWarnings("unchecked")
	public Class<? extends T> getType() {
		return  (Class<? extends T>) dynamicObject.getModel().getDynamicAttribute(attributeName).getClass();
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean newStatus) {
		this.readOnly = newStatus;
	}

}
