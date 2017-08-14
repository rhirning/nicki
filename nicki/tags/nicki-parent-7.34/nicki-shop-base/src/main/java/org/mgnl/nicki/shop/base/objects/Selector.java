/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.base.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("nickiSelector")
public class Selector extends BaseDynamicObject {
	private static final Logger LOG = LoggerFactory.getLogger(Selector.class);

	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@DynamicAttribute(externalName="nickiSelectorValue")
	private String[] value;
	@DynamicAttribute(externalName="nickiSelectorValueProvider")
	private TextArea valueProvider;

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
			LOG.error("Error", e);
		}
		return null;
	}

}
