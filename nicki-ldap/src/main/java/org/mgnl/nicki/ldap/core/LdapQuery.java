
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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.data.Query;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.core.objects.DynamicReference;
import org.mgnl.nicki.ldap.objects.StructuredDynamicReference;

public class LdapQuery implements Query {
	Map<String, List<String>> searchValues = new HashMap<String, List<String>>();
	List<String> filters = new ArrayList<String>();
	private String baseDN;
	private Map<String,String> resultAttributes = new HashMap<String, String>();

	public LdapQuery() {
		super();
	}

	public LdapQuery(String baseDN) {
		super();
		this.baseDN = baseDN;
	}

	public LdapQuery(String path, DynamicReference reference) {
		super();
		this.baseDN = reference.getBaseDn();
		addSearchValue(reference.getExternalName(), path);
	}

	public LdapQuery(String path, StructuredDynamicReference reference) {
		super();
		this.baseDN = reference.getBaseDn();
		addSearchValue(reference.getAttributeName(), path + "*");
	}

	public void addFilter(String filter) {
		if (StringUtils.isNotEmpty(filter)) {
			this.filters.add(filter);
		}
	}
	
	public final void addSearchValue(String key, String value) {
		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
			if (!searchValues.containsKey(key)) {
				this.searchValues.put(key, new ArrayList<String>());
			}
			this.searchValues.get(key).add(value);
		}
	}
	
	public List<String> getSearchValue(String key) {
		return this.searchValues.get(key);
	}

	public Map<String, List<String>> getSearchValues() {
		return searchValues;
	}
	
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

	public String getBaseDN() {
		return baseDN;
	}

	public Map<String, String> getResultAttributes() {
		return resultAttributes;
	}

	public void setResultAttributes(Map<String, String> resultAttributes) {
		this.resultAttributes = resultAttributes;
	}
	
	public void addResultAttribute(String ldapName, String displayName) {
		resultAttributes.put(ldapName, displayName);
	}

}
