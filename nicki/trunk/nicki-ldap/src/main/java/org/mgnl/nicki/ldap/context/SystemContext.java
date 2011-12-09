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

import java.io.Serializable;

import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public final class SystemContext extends BasicContext implements NickiContext, Serializable {

	protected SystemContext(Target target, DynamicObject user, READONLY readonly) throws InvalidPrincipalException {
		super(target, readonly);
		setPrincipal(new NickiPrincipal(getTarget().getProperty("securityPrincipal"),
				getTarget().getProperty("securityCredentials")));
		setUser(user);
	}

}
