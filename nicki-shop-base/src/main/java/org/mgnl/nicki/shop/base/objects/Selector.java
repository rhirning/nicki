
package org.mgnl.nicki.shop.base.objects;

/*-
 * #%L
 * nicki-shop-base
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
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
 * #L%
 */


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.types.TextArea;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class Selector.
 */
@Slf4j
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("nickiSelector")
public class Selector extends BaseDynamicObject {

	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/** The value. */
	@DynamicAttribute(externalName="nickiSelectorValue")
	private String[] value;
	
	/** The value provider. */
	@DynamicAttribute(externalName="nickiSelectorValueProvider")
	private TextArea valueProvider;

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	@SuppressWarnings("unchecked")
	public List<String> getValues() {
		List<String> values = (List<String>) get("value");
		if (values != null && values.size() > 0) {
			return values;
		} else {
			return new ArrayList<String>();
		}
	}

	/**
	 * Checks for value provider.
	 *
	 * @return true, if successful
	 */
	public boolean hasValueProvider() {
		return StringUtils.isNotEmpty(getValueProviderClass());
	}

	/**
	 * Gets the value provider class.
	 *
	 * @return the value provider class
	 */
	public String getValueProviderClass() {
		return (String) get("valueProvider");
	}

	/**
	 * Gets the value provider.
	 *
	 * @return the value provider
	 */
	public ValueProvider getValueProvider() {
		try {
			ValueProvider provider = (ValueProvider)Classes.newInstance(getValueProviderClass());
			return provider;
		} catch (Exception e) {
			log.error("Error", e);
		}
		return null;
	}

}
