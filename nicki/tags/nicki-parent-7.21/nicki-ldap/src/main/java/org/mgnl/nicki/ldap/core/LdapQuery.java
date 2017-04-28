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
package org.mgnl.nicki.ldap.core;

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
