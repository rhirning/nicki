
package org.mgnl.nicki.ldap.query;

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
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.objects.SearchResultEntry;
import org.mgnl.nicki.core.data.Query;
import org.mgnl.nicki.core.data.SearchQueryHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class LdapSearchHandler.
 */
public class LdapSearchHandler extends BasicLdapHandler implements SearchQueryHandler {
	
	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "=";
	
	/** The Constant NO_SCOPE. */
	public static final int NO_SCOPE = -999;

	/** The query. */
	private Query query;
	
	/** The result. */
	private List<SearchResultEntry> result = new ArrayList<SearchResultEntry>();
	
	/** The scope. */
	private SCOPE scope = SCOPE.SUBTREE;

	/**
	 * Instantiates a new ldap search handler.
	 *
	 * @param context the context
	 * @param query the query
	 */
	public LdapSearchHandler(NickiContext context, Query query) {
		super(context);
		this.query = query;
		this.setFilter(query.getFilter());
	}

	/**
	 * Instantiates a new ldap search handler.
	 *
	 * @param context the context
	 * @param query the query
	 * @param scope the scope
	 */
	public LdapSearchHandler(NickiContext context, Query query, SCOPE scope) {
		this(context, query);
		this.setFilter(query.getFilter());
		this.scope = scope;
	}

	/**
	 * Handle.
	 *
	 * @param results the results
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (ContextSearchResult rs : results) {
			SearchResultEntry entry = new SearchResultEntry();
			String dn = rs.getNameInNamespace();
			entry.setDn(dn);
			entry.addValue("dn", dn);
			for (String attributeName :query.getResultAttributes().keySet()) {
				Object value = rs.getValue(Object.class, attributeName);
				entry.addValue(query.getResultAttributes().get(attributeName), value);
			}
			result.add(entry);
		}

	}

	/**
	 * Gets the base DN.
	 *
	 * @return the base DN
	 */
	public String getBaseDN() {
		return query.getBaseDN();
	}

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public List<SearchResultEntry> getResult() {
		return result;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	@Override
	public SCOPE getScope() {
		return scope;
	}


}
