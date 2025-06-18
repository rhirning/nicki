
package org.mgnl.nicki.core.auth;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.security.Principal;

import org.apache.commons.lang3.StringUtils;


/**
 * Principal (user / password).
 */
@SuppressWarnings("serial")
public class NickiPrincipal implements Principal, java.io.Serializable {

	/** The name. */
	private String name;
	
	/** The password. */
	private String password;

	/**
	 * Instantiates a new nicki principal.
	 *
	 * @param name the name
	 * @param password the password
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public NickiPrincipal(String name, String password) throws InvalidPrincipalException {
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
			throw new InvalidPrincipalException();
		}
		this.name = name;
		this.password = password;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return ("NickiPrincipal:  " + getName());
	}

	/**
	 * Equals.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
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
	
	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
}
