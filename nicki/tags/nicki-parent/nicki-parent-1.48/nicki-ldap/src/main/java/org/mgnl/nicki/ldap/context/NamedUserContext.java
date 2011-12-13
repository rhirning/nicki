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

import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class NamedUserContext extends BasicContext implements NickiContext {

	public NamedUserContext(Target target, DynamicObject user, String password) throws InvalidPrincipalException {
		super(target, READONLY.FALSE);
		setPrincipal(new NickiPrincipal(user.getPath(), password));
		setUser(user);
	}
}
