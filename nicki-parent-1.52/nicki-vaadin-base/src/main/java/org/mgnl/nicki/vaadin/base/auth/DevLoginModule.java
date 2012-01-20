/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.vaadin.base.auth;

import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.auth.NickiLoginModule;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.context.AppContext;

public class DevLoginModule extends NickiLoginModule implements LoginModule {

	@Override
	public boolean login() throws LoginException {
		// prompt for a user name and password
		if (getCallbackHandler() == null)
			throw new LoginException("Error: no CallbackHandler available "
					+ "to garner authentication information from the user");
		NickiPrincipal principal = null;
		try {
			 principal = AppContext.getSystemContext().getPrincipal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String username = principal.getName();
		String password = principal.getPassword();

		if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
			setSucceeded(true);
			setUsername(username);
			setPassword(password);
			return true;
		} else {
			setSucceeded(false);
			username = null;
			password = null;
			setUsername(username);
			setPassword(password);
			return false;
		}
	}
}
