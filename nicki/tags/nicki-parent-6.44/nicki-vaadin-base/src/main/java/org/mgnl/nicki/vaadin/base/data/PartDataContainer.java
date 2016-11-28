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
package org.mgnl.nicki.vaadin.base.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.DynamicObject;

import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class PartDataContainer extends AttributeDataContainer<String> implements DataContainer<String>, Property<String> {
	private String separator;
	private String name;

	public PartDataContainer(DynamicObject dynamicObject, String attributeName, String name, String separator) {
		super(dynamicObject, attributeName);
		this.separator = separator;
		this.name = name;
	}

	@Override
	public String getValue() {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) getDynamicObject().get(getAttributeName());
		for (String value : list) {
			String name = StringUtils.substringBefore(value, this.separator);
			if (StringUtils.equals(name, this.name)) {
				String data = StringUtils.substringAfter(value, this.separator);
				return data;
			}
		}

		return null;
	}

	@Override
	public void setValue(String newValue) {
		String value = (String) newValue;
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) getDynamicObject().get(getAttributeName());
		List<String> remover = new ArrayList<String>();
		for (String oldValue : list) {
			String name = StringUtils.substringBefore(oldValue, "=");
			if (StringUtils.equals(name, this.name)) {
				remover.add(oldValue);
			}
		}
		for (String toBeRemoved : remover) {
			list.remove(toBeRemoved);			
		}
		if (StringUtils.isNotEmpty(value)) {
			list.add(name + separator + value);
		}
	}

}
