/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.ldap.objects.DynamicReference;
import org.mgnl.nicki.ldap.objects.StructuredDynamicReference;

public class LdapQuery {
	Map<String, List<String>> searchValues = new HashMap<String, List<String>>();
	List<String> filters = new ArrayList<String>();
	private String baseDN;
	private Map<String,String> resultAttributes = new HashMap<String, String>();

	public LdapQuery(String baseDN) {
		super();
		this.baseDN = baseDN;
	}

	public LdapQuery(String path, DynamicReference reference) {
		super();
		this.baseDN = reference.getBaseDn();
		addSearchValue(reference.getAttributeName(), path);
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
	
	public void addSearchValue(String key, String value) {
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
		StringBuffer outerSb = new StringBuffer();

		for (Iterator<String> iterator = filters.iterator(); iterator.hasNext();) {
			String filter =  iterator.next();
			LdapHelper.addQuery(outerSb, filter, LOGIC.AND);
		}

		for (Iterator<String> iterator = searchValues.keySet().iterator(); iterator.hasNext();) {
			StringBuffer sb = new StringBuffer();
			String key =  iterator.next();
			List<String> list = searchValues.get(key);
			for (Iterator<String> iterator2 = list.iterator(); iterator2.hasNext();) {
				String value = iterator2.next();
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
