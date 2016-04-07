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
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.objects.SearchResultEntry;
import org.mgnl.nicki.core.data.Query;
import org.mgnl.nicki.core.data.SearchQueryHandler;

public class LdapSearchHandler extends BasicLdapHandler implements SearchQueryHandler {
	public static final String SEPARATOR = "=";
	public static final int NO_SCOPE = -999;

	private Query query;
	private List<SearchResultEntry> result = new ArrayList<SearchResultEntry>();
	private SCOPE scope = SCOPE.SUBTREE;

	public LdapSearchHandler(NickiContext context, Query query) {
		super(context);
		this.query = query;
		this.setFilter(query.getFilter());
	}

	public LdapSearchHandler(NickiContext context, Query query, SCOPE scope) {
		this(context, query);
		this.setFilter(query.getFilter());
		this.scope = scope;
	}

	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (ContextSearchResult rs : results) {
			SearchResultEntry entry = new SearchResultEntry();
			String dn = rs.getNameInNamespace();
			entry.setDn(dn);
			entry.addValue("dn", dn);
			for (String attributeName :query.getResultAttributes().keySet()) {
				Object value = rs.getValue(attributeName);
				entry.addValue(query.getResultAttributes().get(attributeName), value);
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