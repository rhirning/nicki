/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.core.auth;

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
