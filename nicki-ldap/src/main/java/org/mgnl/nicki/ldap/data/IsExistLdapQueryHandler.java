/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.data;

import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.QueryHandler;
import org.mgnl.nicki.ldap.core.BasicLdapHandler;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;


public class IsExistLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	private String dn = null;

	private boolean exist = false;

	public IsExistLdapQueryHandler(NickiContext context, String path) {
		super(context);
		this.dn = path;
	}


	public String getBaseDN() {
		return this.dn;
	}

	public void handle(List<ContextSearchResult> results) {
		try {
			if (results != null && results.size() > 0) {
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
		return constraints;
	}


	public boolean isExist() {
		return exist;
	}
}
