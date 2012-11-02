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
package org.mgnl.nicki.shop.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicTemplateObject;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.types.TextArea;

@SuppressWarnings("serial")
@DynamicObject(target="edir")
public class Selector extends DynamicTemplateObject {
	public void initDataModel() {
		addObjectClass("nickiSelector");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("value", "nickiSelectorValue", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("valueProvider", "nickiSelectorValueProvider", TextArea.class);
		addAttribute(dynAttribute);
		
	}

	@SuppressWarnings("unchecked")
	public List<String> getValues() {
		List<String> values = (List<String>) get("value");
		if (values != null && values.size() > 0) {
			return values;
		} else {
			return new ArrayList<String>();
		}
	}

	public boolean hasValueProvider() {
		return StringUtils.isNotEmpty(getValueProviderClass());
	}

	public String getValueProviderClass() {
		return (String) get("valueProvider");
	}

	public ValueProvider getValueProvider() {
		try {
			ValueProvider provider = (ValueProvider)Classes.newInstance(getValueProviderClass());
			return provider;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
