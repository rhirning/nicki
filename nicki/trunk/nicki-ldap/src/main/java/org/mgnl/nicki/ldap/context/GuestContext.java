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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.mgnl.nicki.ldap.objects.DynamicObjectException;

@SuppressWarnings("serial")
public class GuestContext extends BasicContext implements NickiContext {

	protected GuestContext(Target target, READONLY readonly) {
		super(target, readonly);
	}

	public DirContext getDirContext() throws DynamicObjectException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, getTarget().getProperty("providerUrl"));
		// Enable connection pooling
		env.put("com.sun.jndi.ldap.connect.pool", "true");

		try {
			return new InitialDirContext(env);
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

}
