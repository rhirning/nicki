package org.mgnl.nicki.core.auth;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2018 Ralf Hirning
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

import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.mgnl.nicki.core.context.AppContext;

import lombok.extern.slf4j.Slf4j;


/**
 * LoginModule to grant anonymous access.
 */
@Slf4j
public class OpenLoginModule extends NickiLoginModule implements LoginModule {

	/**
	 * Login.
	 *
	 * @return true, if successful
	 * @throws LoginException the login exception
	 */
	@Override
	public boolean login() throws LoginException {
		try {
			NickiPrincipal nickiPrincipal = new NickiPrincipal("anonymous", "none");
			setPrincipal(new DynamicObjectPrincipal(nickiPrincipal, AppContext.getSystemContext(), AppContext.getSystemContext()));
			//getSubject().getPrincipals().add(principal);
			setSucceeded(true);
			return true;
		} catch (InvalidPrincipalException e) {
			log.error("Invalid principal", e);
		}
		return false;
	}

}
