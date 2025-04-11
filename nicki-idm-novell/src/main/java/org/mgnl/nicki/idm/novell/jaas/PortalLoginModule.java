
package org.mgnl.nicki.idm.novell.jaas;

/*-
 * #%L
 * nicki-idm-novell
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


import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiLoginCallbackHandler;
import org.mgnl.nicki.core.auth.NickiLoginModule;
import org.mgnl.nicki.core.auth.NickiPrincipal;

// TODO: Auto-generated Javadoc
/**
 * The Class PortalLoginModule.
 */
public class PortalLoginModule extends NickiLoginModule implements LoginModule {

	/**
	 * Login.
	 *
	 * @return true, if successful
	 * @throws LoginException the login exception
	 */
	@Override
	public boolean login() throws LoginException {
		// prompt for a user name and password
		if (getCallbackHandler() == null)
			throw new LoginException("Error: no CallbackHandler available "
					+ "to garner authentication information from the user");

		if (getCallbackHandler() instanceof NickiLoginCallbackHandler) {
			((NickiLoginCallbackHandler) getCallbackHandler()).setAdapter(new UserAppAdapter());
		}
		
		Callback[] callbacks = new Callback[2];

		try {
			callbacks[0] = new NameCallback("name");
			callbacks[1] = new PasswordCallback("password", false);
			getCallbackHandler().handle(callbacks);
		} catch (Exception e) {
			return false;
		}
		String username = ((NameCallback) callbacks[0]).getName();
		String password = new String(((PasswordCallback) callbacks[1]).getPassword());
		try {
			setLoginContext(login(new NickiPrincipal(username, password)));
			setSucceeded(true);
			return true;
		} catch (InvalidPrincipalException e) {
			setSucceeded(false);
			return false;
		}
	}
}
