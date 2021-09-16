package org.mgnl.nicki.ldap.context;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;

public class NickiLdapContext extends InitialLdapContext implements AutoCloseable {

	public NickiLdapContext() throws NamingException {
		super();
	}

	public NickiLdapContext(Hashtable<String, String> env, Control[] connCtls) throws NamingException {
		super(env,connCtls);
	}

}
