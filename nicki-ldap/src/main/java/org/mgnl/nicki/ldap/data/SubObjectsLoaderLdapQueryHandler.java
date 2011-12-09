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

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.NickiContext;

public class SubObjectsLoaderLdapQueryHandler extends ObjectsLoaderLdapQueryHandler {

	public SubObjectsLoaderLdapQueryHandler(NickiContext context, String parent, String filter) {
		super(context, parent, filter);
	}

	@Override
	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		// Specify the ids of the attributes to return
		String[] attrIDs = { "objectClass" };
		constraints.setReturningAttributes(attrIDs);
		return constraints;
	}
}
