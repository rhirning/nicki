/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.context;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class ContextEnumeration<T> {
	NamingEnumeration<SearchResult> results;
	
	public ContextEnumeration(NamingEnumeration<SearchResult> results) {
		this.results = results;
	}

	public boolean hasMore() throws DynamicObjectException {
		try {
			return this.results.hasMore();
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

	public SearchResult next() {
		return this.next();
	}

}
