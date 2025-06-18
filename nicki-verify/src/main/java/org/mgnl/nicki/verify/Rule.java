
package org.mgnl.nicki.verify;

/*-
 * #%L
 * nicki-verify
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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


/**
 * The Class Rule.
 */
@SuppressWarnings("serial")
public abstract class Rule implements Serializable {
	
	/** The Constant MAP_SEPARATOR. */
	public static final String MAP_SEPARATOR = "!";
	
	/** The Constant MAP_EQUAL. */
	public static final String MAP_EQUAL = "=";
	
	/** The parameter. */
	private String parameter;
	
	/** The map. */
	private Map<String, String> map = new HashMap<String, String>();
	
	/**
	 * Instantiates a new rule.
	 */
	public Rule() {
		super();
	}

	/**
	 * Evaluate.
	 *
	 * @param value the value
	 * @param values the values
	 * @return true, if successful
	 */
	public abstract boolean evaluate(String value, Map<String, String> values);
	
	/**
	 * Gets the long.
	 *
	 * @param value the value
	 * @return the long
	 */
	public static long getLong(String value) {
		value = StringUtils.stripToNull(value);
		if (value == null) {
			return 0;
		}
		if (StringUtils.contains(value, ",")) {
			value = StringUtils.substringBefore(value, ",");
		}
		if (StringUtils.contains(value, ".")) {
			value = StringUtils.remove(value, ".");
		}
		return Long.valueOf(value);
	}
	
	/**
	 * Gets the number.
	 *
	 * @param value the value
	 * @return the number
	 * @throws NumberFormatException the number format exception
	 */
	public static double getNumber(String value) throws NumberFormatException {
		value = StringUtils.stripToNull(value);
		if (value == null) {
			return 0;
		}
		if (StringUtils.contains(value, ".")) {
			value = StringUtils.remove(value, ".");
		}
		if (StringUtils.contains(value, ",")) {
			value = StringUtils.replace(value, ",", ".");
		}
		return Double.valueOf(value);
	}

	/**
	 * Gets the parameter.
	 *
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * Sets the parameter.
	 *
	 * @param parameter the new parameter
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
		initMap();
	}

	/**
	 * Inits the map.
	 */
	private void initMap() {
		map.clear();
		if (this.parameter != null) {
			String [] entries = StringUtils.split(parameter, MAP_SEPARATOR);
			for (String entry : entries) {
				if (StringUtils.contains(entry, MAP_EQUAL)) {
					map.put(StringUtils.substringBefore(entry, MAP_EQUAL), 
							StringUtils.substringAfter(entry, MAP_EQUAL));
				}
			}
		}
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public abstract String getMessage();
	

	/**
	 * Gets the i 18 n base.
	 *
	 * @return the i 18 n base
	 */
	public String getI18nBase() {
		return "nicki.verify.message";
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public Map<String, String> getMap() {
		return map;
	}

}
