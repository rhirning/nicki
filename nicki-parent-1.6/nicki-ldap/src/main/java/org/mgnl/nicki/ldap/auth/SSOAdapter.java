package org.mgnl.nicki.ldap.auth;

public interface SSOAdapter {
	String getName(Object request);
	char[] getPassword(Object request);
}
