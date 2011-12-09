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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.QueryHandler;
import org.mgnl.nicki.ldap.objects.ContextAttribute;
import org.mgnl.nicki.ldap.objects.ContextAttributes;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class LdapSearchHandler extends BasicLdapHandler implements QueryHandler {
	public static final String SEPARATOR = "=";
	public static final int NO_SCOPE = -999;

	private LdapQuery query;
	private List<SearchResultEntry> result = new ArrayList<SearchResultEntry>();
	private int scope = NO_SCOPE;

	public LdapSearchHandler(NickiContext context, LdapQuery query) {
		super(context);
		this.query = query;
		this.setFilter(query.getFilter());
	}

	public LdapSearchHandler(NickiContext context, LdapQuery query, int scope) {
		this(context, query);
		this.setFilter(query.getFilter());
		this.scope = scope;
	}

	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (Iterator<ContextSearchResult> resultsIterator = results.iterator(); resultsIterator.hasNext();) {
			ContextSearchResult rs = resultsIterator.next();
			SearchResultEntry entry = new SearchResultEntry();
			ContextAttributes attrs = rs.getAttributes();
			String dn = rs.getNameInNamespace();
			entry.setDn(dn);
			entry.addValue("dn", dn);
			for (Iterator<String> iterator = query.getResultAttributes().keySet().iterator(); iterator.hasNext();) {
				String attributeName = iterator.next();
				ContextAttribute attr = attrs.get(attributeName);
				if (attr != null) {
					Enumeration<Object> vals = (Enumeration<Object>) attr.getAll();
					if (vals.hasMoreElements()) {
						Object value = vals.nextElement();
						entry.addValue(query.getResultAttributes().get(attributeName), value);
					}
				}
			}
			result.add(entry);
		}

	}

	public String getBaseDN() {
		return query.getBaseDN();
	}

	public List<SearchResultEntry> getResult() {
		return result;
	}

	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		if (this.scope != NO_SCOPE) {
			constraints.setSearchScope(this.scope);
		} else {
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		}
		return constraints;
	}


}
