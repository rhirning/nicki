package org.mgnl.nicki.ldap.context;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.context.ContextFactory;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.objects.DynamicObject;

public class LdapContextFactory implements ContextFactory {

	@Override
	public NickiContext getGuestContext(Target target) {
		return new LdapGuestContext(target);
	}

	@Override
	public NickiContext getNamedUserContext(Target target, DynamicObject user, String password)
			throws InvalidPrincipalException {
		return new LdapNamedUserContext(target, user, password);
	}

	@Override
	public NickiContext getSystemContext(Target target, DynamicObject user) throws InvalidPrincipalException {
		return new LdapSystemContext(target, user);
	}

}
