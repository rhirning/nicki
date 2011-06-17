package org.mgnl.nicki.ldap.context;

import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;

@SuppressWarnings("serial")
public class NamedUserContext extends BasicContext implements NickiContext {

	public NamedUserContext(Target target, String name, String password, READONLY readonly) throws InvalidPrincipalException {
		super(target, readonly);
		setPrincipal(new NickiPrincipal(name, password));
	}
}
