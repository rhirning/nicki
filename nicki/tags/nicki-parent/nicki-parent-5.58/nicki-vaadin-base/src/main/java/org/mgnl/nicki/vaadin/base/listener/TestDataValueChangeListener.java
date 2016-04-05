/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Field;

@SuppressWarnings("serial")
public class TestDataValueChangeListener implements ValueChangeListener {

	private Property<Map<String, String>> dataContainer;
	private ComponentContainer testData;
	public TestDataValueChangeListener(Property<Map<String, String>> dataContainer, ComponentContainer testData) {
		this.dataContainer = dataContainer;
		this.testData = testData;
	}

	public void valueChange(ValueChangeEvent event) {
		dataContainer.setValue(collectMapValues(testData));
	}

	public Map<String, String> collectMapValues(ComponentContainer cont) {
		Map<String, String> map = new HashMap<String, String>();
		for (Component component : cont) {
			String caption = component.getCaption();
			if (component instanceof Field) {
				@SuppressWarnings("unchecked")
				String value = ((Field<String>) component).getValue();
				map.put(caption, value);
			}
		}
		return map;
	}

	public List<String> collectValues(ComponentContainer cont) {
		List<String> list = new ArrayList<String>();
		for (Component component : cont) {
			if (component instanceof Field) {
				@SuppressWarnings("unchecked")
				String value = ((Field<String>) component).getValue();
				list.add(value);
			}
		}
		return list;
	}

	
}
