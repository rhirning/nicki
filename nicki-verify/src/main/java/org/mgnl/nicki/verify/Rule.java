
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

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public abstract class Rule implements Serializable {
	public static final String MAP_SEPARATOR = "!";
	public static final String MAP_EQUAL = "=";
	private String parameter;
	private Map<String, String> map = new HashMap<String, String>();
	
	public Rule() {
		super();
	}

	public abstract boolean evaluate(String value, Map<String, String> values);
	
	public static long getLong(String value) {
		value = StringUtils.strip(value);
		if (StringUtils.contains(value, ",")) {
			value = StringUtils.substringBefore(value, ",");
		}
		if (StringUtils.contains(value, ".")) {
			value = StringUtils.remove(value, ".");
		}
		return Long.valueOf(value);
	}
	
	public static double getNumber(String value) throws NumberFormatException {
		value = StringUtils.strip(value);
		if (StringUtils.contains(value, ".")) {
			value = StringUtils.remove(value, ".");
		}
		if (StringUtils.contains(value, ",")) {
			value = StringUtils.replace(value, ",", ".");
		}
		return Double.valueOf(value);
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
		initMap();
	}

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

	public abstract String getMessage();
	

	public String getI18nBase() {
		return "nicki.verify.message";
	}

	public Map<String, String> getMap() {
		return map;
	}

}
