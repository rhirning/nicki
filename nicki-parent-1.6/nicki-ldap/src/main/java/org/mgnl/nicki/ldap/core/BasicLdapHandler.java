package org.mgnl.nicki.ldap.core;

import org.mgnl.nicki.ldap.context.NickiContext;

public class BasicLdapHandler {
	private NickiContext context;

	public BasicLdapHandler(NickiContext context) {
		super();
		this.context = context;
	}

	public NickiContext getContext() {
		return context;
	}


}
