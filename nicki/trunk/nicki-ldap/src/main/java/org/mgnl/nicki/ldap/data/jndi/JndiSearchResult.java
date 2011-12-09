/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.data.jndi;

import javax.naming.directory.SearchResult;

import org.mgnl.nicki.ldap.objects.ContextAttributes;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;

public class JndiSearchResult implements ContextSearchResult {
	private SearchResult rs;

	public JndiSearchResult(SearchResult rs) {
		this.rs = rs;
	}

	public String getNameInNamespace() {
		return rs.getNameInNamespace();
	}

	public ContextAttributes getAttributes() {		
		return new JndiAttributes(rs.getAttributes());
	}

}
