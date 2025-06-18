
package org.mgnl.nicki.core.helper;

/*-
 * #%L
 * nicki-core
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;


/**
 * The Class GenericAttributeMapper.
 */
public class GenericAttributeMapper  implements AttributeMapper{
	
	/** The internal to external. */
	private Map<String, String> internalToExternal = new HashMap<String, String>();
	
	/** The external to internal. */
	private Map<String, String> externalToInternal = new HashMap<String, String>();
	
	/** The hidden internal attributes. */
	private Collection<String> hiddenInternalAttributes = new ArrayList<String>(); 
	
	/** The hidden external attributes. */
	private Collection<String> hiddenExternalAttributes = new ArrayList<String>();
	
	/** The external attributes. */
	private Collection<String> externalAttributes = new ArrayList<String>();
	
	/**
	 * Instantiates a new generic attribute mapper.
	 */
	protected GenericAttributeMapper() {
	}
	
	/**
	 * Instantiates a new generic attribute mapper.
	 *
	 * @param pathToMap the path to map
	 * @param configBase the config base
	 */
	public GenericAttributeMapper(String pathToMap, String configBase) {
		Properties properties = new Properties() ;
		try {
			properties.load(GenericAttributeMapper.class.getResourceAsStream(pathToMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		init(properties, configBase);
	}
	
	/**
	 * Inits the.
	 *
	 * @param properties the properties
	 * @param configBase the config base
	 */
	protected void init(Properties properties, String configBase) {

		for (Entry<Object, Object> entry : properties.entrySet()) {
			add((String) entry.getKey(), (String) entry.getValue());
		}
		
		List<String> rawHiddenExternalAttributes = Config.getList(configBase + ".attributes.hidden", ",");
		for (String attribute : rawHiddenExternalAttributes) {
			String internal = toInternalName(attribute);
			String external = toExternalName(internal);
			hiddenInternalAttributes.add(internal);
			hiddenExternalAttributes.add(external);
		}
		externalAttributes.addAll(internalToExternal.values());
		List<String> toBeRemoved = new ArrayList<String>();
		for (String key : externalAttributes) {
			if (hiddenExternalAttributes.contains(key)) {
				toBeRemoved.add(key);
			}
		}
		
		for (String string : toBeRemoved) {
			externalAttributes.remove(string);
		}
		
		
	}

	/**
	 * Correct value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the string
	 */
	@Override
	public String correctValue(String key, String value) {
		return value;
	}

	/**
	 * Correct external.
	 *
	 * @param name the name
	 * @return the string
	 */
	public String correctExternal(String name) {
		return toExternal(toInternal(name));
	}
	
	/**
	 * Gets the external attributes.
	 *
	 * @return the external attributes
	 */
	public String[] getExternalAttributes() {
		return externalAttributes.toArray(new String[0]);
	}
	
	/**
	 * Adds the.
	 *
	 * @param object the object
	 * @param object2 the object 2
	 */
	private void add(String object, String object2) {
		internalToExternal.put(object.toLowerCase(), object2);
		externalToInternal.put(object2.toLowerCase(), object);
	}
	
	/**
	 * Checks if is hidden internal.
	 *
	 * @param internal the internal
	 * @return true, if is hidden internal
	 */
	@Override
	public boolean isHiddenInternal(String internal) {
		return hiddenExternalAttributes.contains(toExternal(internal));
	}

	/**
	 * Checks if is hidden external.
	 *
	 * @param external the external
	 * @return true, if is hidden external
	 */
	@Override
	public boolean isHiddenExternal(String external) {
		return hiddenInternalAttributes.contains(toInternal(external));
	}

	/**
	 * To external.
	 *
	 * @param internal the internal
	 * @return the string
	 */
	@Override
	public String toExternal(String internal) {
		return toExternalName(internal);
	}

	/**
	 * To internal.
	 *
	 * @param external the external
	 * @return the string
	 */
	@Override
	public String toInternal(String external) {
		return toInternalName(external);
	}

	/**
	 * To external name.
	 *
	 * @param internal the internal
	 * @return the string
	 */
	private String toExternalName(String internal) {
		String key = StringUtils.trimToEmpty(internal).toLowerCase();
		if (internalToExternal.containsKey(key)) {
			return internalToExternal.get(key);
		} else {
			return null;
		}
	}

	/**
	 * To internal name.
	 *
	 * @param external the external
	 * @return the string
	 */
	private String toInternalName(String external) {
		String key = StringUtils.trimToEmpty(external).toLowerCase();
		if (externalToInternal.containsKey(key)) {
			return externalToInternal.get(key);
		} else {
			return null;
		}
	}

	/**
	 * Checks for external.
	 *
	 * @param external the external
	 * @return true, if successful
	 */
	@Override
	public boolean hasExternal(String external) {
		String key = StringUtils.trimToEmpty(external).toLowerCase();
		return externalToInternal.containsKey(key);
	}

	/**
	 * Checks for internal.
	 *
	 * @param internal the internal
	 * @return true, if successful
	 */
	@Override
	public boolean hasInternal(String internal) {
		String key = StringUtils.trimToEmpty(internal).toLowerCase();
		return internalToExternal.containsKey(key);
	}

	/**
	 * Checks if is strict.
	 *
	 * @return true, if is strict
	 */
	@Override
	public boolean isStrict() {
		return true;
	}
}
