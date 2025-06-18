
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


import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;


/**
 * CallbackHandler.
 */
public class NickiSSOLoginCallbackHandler extends NickiLoginCallbackHandler implements CallbackHandler {

	/**
	 * Instantiates a new nicki SSO login callback handler.
	 *
	 * @param request the request
	 */
	public NickiSSOLoginCallbackHandler(Object request) {
		super(request);
	}

	/**
	 * Handle.
	 *
	 * @param callbacks the callbacks
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedCallbackException the unsupported callback exception
	 */
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof NameCallback) {
				NameCallback callback = (NameCallback) callbacks[i];
				callback.setName(getAdapter().getName());
			} else 	if (callbacks[i] instanceof PasswordCallback) {
				PasswordCallback callback = (PasswordCallback) callbacks[i];
				callback.setPassword(getAdapter().getPassword());
			} else {
				throw new UnsupportedCallbackException(callbacks[i],
						"Unrecognized Callback");
			}
		}
	}
}
