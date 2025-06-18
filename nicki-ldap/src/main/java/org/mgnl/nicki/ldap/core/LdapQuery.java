
package org.mgnl.nicki.ldap.core;

/*-
 * #%L
 * nicki-ldap
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.data.Query;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.core.objects.DynamicReference;
import org.mgnl.nicki.ldap.objects.StructuredDynamicReference;


/**
 * The Class LdapQuery.
 */
public class LdapQuery implements Query {
	
	/** The search values. */
	Map<String, List<String>> searchValues = new HashMap<String, List<String>>();
	
	/** The filters. */
	List<String> filters = new ArrayList<String>();
	
	/** The base DN. */
	private String baseDN;
	
	/** The result attributes. */
	private Map<String,String> resultAttributes = new HashMap<String, String>();

	/**
	 * Instantiates a new ldap query.
	 */
	public LdapQuery() {
		super();
	}

	/**
	 * Instantiates a new ldap query.
	 *
	 * @param baseDN the base DN
	 */
	public LdapQuery(String baseDN) {
		super();
		this.baseDN = baseDN;
	}

	/**
	 * Instantiates a new ldap query.
	 *
	 * @param path the path
	 * @param reference the reference
	 */
	public LdapQuery(String path, DynamicReference reference) {
		super();
		this.baseDN = reference.getBaseDn();
		addSearchValue(reference.getExternalName(), path);
	}

	/**
	 * Instantiates a new ldap query.
	 *
	 * @param path the path
	 * @param reference the reference
	 */
	public LdapQuery(String path, StructuredDynamicReference reference) {
		super();
		this.baseDN = reference.getBaseDn();
		addSearchValue(reference.getAttributeName(), path + "*");
	}

	/**
	 * Adds the filter.
	 *
	 * @param filter the filter
	 */
	public void addFilter(String filter) {
		if (StringUtils.isNotEmpty(filter)) {
			this.filters.add(filter);
		}
	}
	
	/**
	 * Adds the search value.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public final void addSearchValue(String key, String value) {
		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
			if (!searchValues.containsKey(key)) {
				this.searchValues.put(key, new ArrayList<String>());
			}
			this.searchValues.get(key).add(value);
		}
	}
	
	/**
	 * Gets the search value.
	 *
	 * @param key the key
	 * @return the search value
	 */
	public List<String> getSearchValue(String key) {
		return this.searchValues.get(key);
	}

	/**
	 * Gets the search values.
	 *
	 * @return the search values
	 */
	public Map<String, List<String>> getSearchValues() {
		return searchValues;
	}
	
	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public String getFilter() {
		StringBuilder outerSb = new StringBuilder();

		for (String filter : filters) {
			LdapHelper.addQuery(outerSb, filter, LOGIC.AND);
		}

		for (String key : searchValues.keySet()) {
			StringBuilder sb = new StringBuilder();
			for (String value : searchValues.get(key)) {
				LdapHelper.addQuery(sb, key + "=" + value, LOGIC.OR);
			}
			LdapHelper.addQuery(outerSb, sb.toString(), LOGIC.AND);
		}
		return outerSb.toString();			
			
	}

	/**
	 * Gets the base DN.
	 *
	 * @return the base DN
	 */
	public String getBaseDN() {
		return baseDN;
	}

	/**
	 * Gets the result attributes.
	 *
	 * @return the result attributes
	 */
	public Map<String, String> getResultAttributes() {
		return resultAttributes;
	}

	/**
	 * Sets the result attributes.
	 *
	 * @param resultAttributes the result attributes
	 */
	public void setResultAttributes(Map<String, String> resultAttributes) {
		this.resultAttributes = resultAttributes;
	}
	
	/**
	 * Adds the result attribute.
	 *
	 * @param ldapName the ldap name
	 * @param displayName the display name
	 */
	public void addResultAttribute(String ldapName, String displayName) {
		resultAttributes.put(ldapName, displayName);
	}

}
