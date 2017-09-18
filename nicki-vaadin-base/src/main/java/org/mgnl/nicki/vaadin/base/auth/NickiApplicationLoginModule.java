
package org.mgnl.nicki.vaadin.base.auth;

/*-
 * #%L
 * nicki-vaadin-base
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
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiLoginModule;
import org.mgnl.nicki.core.auth.NickiPrincipal;

public class NickiApplicationLoginModule extends NickiLoginModule implements LoginModule {

	@Override
	public boolean login() throws LoginException {
		// prompt for a user name and password
		if (getCallbackHandler() == null)
			throw new LoginException("Error: no CallbackHandler available "
					+ "to garner authentication information from the user");
		
		Callback[] callbacks = new Callback[2];

		try {
			callbacks[0] = new CredentialsCallback();
			getCallbackHandler().handle(callbacks);
		} catch (Exception e) {
			return false;
		}
		String username = ((CredentialsCallback) callbacks[0]).getName();
		String password = new String(((CredentialsCallback) callbacks[0]).getPassword());
		try {
			setContext(login(new NickiPrincipal(username, password)));
			setSucceeded(true);
			return true;
		} catch (InvalidPrincipalException e) {
			setSucceeded(false);
			return false;
		}
	}
}
