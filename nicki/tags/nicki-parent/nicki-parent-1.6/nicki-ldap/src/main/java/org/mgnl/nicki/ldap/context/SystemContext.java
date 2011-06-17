package org.mgnl.nicki.ldap.context;

import java.io.Serializable;

import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;

@SuppressWarnings("serial")
public final class SystemContext extends BasicContext implements NickiContext, Serializable {

	protected SystemContext(Target target, READONLY readonly) throws InvalidPrincipalException {
		super(target, readonly);
		setPrincipal(new NickiPrincipal(getTarget().getProperty("securityPrincipal"),
				getTarget().getProperty("securityCredentials")));
	}

}
