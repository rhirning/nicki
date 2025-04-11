
package org.mgnl.nicki.core.objects;

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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchResultEntry.
 */
public class SearchResultEntry implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6784848780254677616L;
	
	/** The values. */
	private Map<String, Object> values = new HashMap<String, Object>();
	
	/** The dn. */
	private String dn;
	
	/**
	 * Gets the dn.
	 *
	 * @return the dn
	 */
	public String getDn() {
		return dn;
	}

	/**
	 * Sets the dn.
	 *
	 * @param dn the new dn
	 */
	public void setDn(String dn) {
		this.dn = dn;
	}

	/**
	 * Adds the value.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void addValue(String key, Object value) {
		this.values.put(key, value);
	}
	
	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(String key) {
		return this.values.get(key);
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public Map<String, Object> getValues() {
		return values;
	}
	
}
