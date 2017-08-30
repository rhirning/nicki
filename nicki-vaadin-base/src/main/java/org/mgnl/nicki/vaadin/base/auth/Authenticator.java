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
package org.mgnl.nicki.vaadin.base.auth;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authenticator {
	private static final Logger LOG = LoggerFactory.getLogger(Authenticator.class);

	public static void authenticate(String name, int count, CallbackHandler callbackHandler) throws LoginException {
		LoginContext loginContext;
		if (callbackHandler != null) {
			loginContext = new LoginContext(name, callbackHandler); 
		} else {
			throw new LoginException("Missing CallbackHandler");
		}
		for (int i = 0; i < count; i++) {
			try {
	              // attempt authentication
	              loginContext.login();

	              // if we return with no exception,
	              // authentication succeeded
	              return;

	          } catch (LoginException le) {

	              LOG.error("Authentication failed: " + le.getMessage());
	              try {
	                  Thread.sleep(3000);
	              } catch (Exception e) {
	            	  LOG.debug("Error", e);
	                  // ignore
	              }

	          }
		}
		throw new LoginException("Too many attempts");
	}

}
