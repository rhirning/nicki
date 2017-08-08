package org.mgnl.nicki.core.auth;

import java.security.Principal;

import org.mgnl.nicki.core.context.NickiContext;

public class DynamicObjectPrincipal implements Principal {
	
	private NickiPrincipal nickiPrincipal;
	private NickiContext loginContext;
	private NickiContext context;

	public DynamicObjectPrincipal(NickiPrincipal nickiPrincipal, NickiContext loginContext, NickiContext context) {
		super();
		this.nickiPrincipal = nickiPrincipal;
		this.loginContext = loginContext;
		this.context = context;
	}

	public DynamicObjectPrincipal(String name, NickiContext loginContext, NickiContext context) {
		super();
		try {
			this.nickiPrincipal = new NickiPrincipal(name, "invalid");
		} catch (InvalidPrincipalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.loginContext = loginContext;
		this.context = context;
	}

	@Override
	public String getName() {
		return this.nickiPrincipal.getName();
	}

	public NickiContext getLoginContext() {
		return loginContext;
	}

	public NickiContext getContext() {
		return context;
	}
	

}
