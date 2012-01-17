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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.vaadin.base.data.DataContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class AttributeInputListener implements ValueChangeListener {

	DataContainer property = null;
	private DynamicObjectValueChangeListener objectListener = null;

	public AttributeInputListener(DataContainer property, DynamicObjectValueChangeListener objectListener) {
		this.property = property;
		this.objectListener = objectListener;
	}

	public void valueChange(ValueChangeEvent event) {
		String value = StringUtils.trimToEmpty((String) event.getProperty().getValue());
		property.setValue(value);
		if (objectListener != null) {
			objectListener.valueChange(property.getDynamicObject(), property.getAttributeName(), value);
		}
	}

}
