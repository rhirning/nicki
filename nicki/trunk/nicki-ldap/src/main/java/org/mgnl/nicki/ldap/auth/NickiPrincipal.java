/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.auth;

import java.security.Principal;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class NickiPrincipal implements Principal, java.io.Serializable {

	private String name;
	private String password;

	public NickiPrincipal(String name, String password) throws InvalidPrincipalException {
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
			throw new InvalidPrincipalException();
		}
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return ("NickiPrincipal:  " + getName());
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (this == o)
			return true;

		if (!(o instanceof NickiPrincipal))
			return false;
		NickiPrincipal that = (NickiPrincipal) o;

		if (this.getName().equals(that.getName()))
			return true;
		return false;
	}
	public int hashCode() {
		return getName().hashCode();
	}

	public String getPassword() {
		return password;
	}
}
