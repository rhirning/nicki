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
		return constraints;
	}
}
