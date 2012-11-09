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
package org.mgnl.nicki.ldap.query;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextAttribute;
import org.mgnl.nicki.core.objects.ContextAttributes;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.objects.SearchResultEntry;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.ldap.data.Query;
import org.mgnl.nicki.ldap.data.QueryHandler;

public class LdapSearchHandler extends BasicLdapHandler implements QueryHandler {
	public static final String SEPARATOR = "=";
	public static final int NO_SCOPE = -999;

	private Query query;
	private List<SearchResultEntry> result = new ArrayList<SearchResultEntry>();
	private SCOPE scope = SCOPE.SUBTREE;

	public LdapSearchHandler(NickiContext context, Query query) {
		super((LdapContext) context);
		this.query = query;
		this.setFilter(query.getFilter());
	}

	public LdapSearchHandler(NickiContext context, Query query, SCOPE scope) {
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

	@Override
	public SCOPE getScope() {
		return scope;
	}


}
