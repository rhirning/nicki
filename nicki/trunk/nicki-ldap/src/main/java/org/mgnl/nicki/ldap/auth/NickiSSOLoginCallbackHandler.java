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

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class NickiSSOLoginCallbackHandler extends NickiLoginCallbackHandler implements CallbackHandler {

	public NickiSSOLoginCallbackHandler(Object request) {
		super(request);
	}

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof NameCallback) {
				NameCallback callback = (NameCallback) callbacks[i];
				callback.setName(getAdapter().getName(getRequest()));
			} else 	if (callbacks[i] instanceof PasswordCallback) {
				PasswordCallback callback = (PasswordCallback) callbacks[i];
				callback.setPassword(getAdapter().getPassword(getRequest()));
			} else {
				throw new UnsupportedCallbackException(callbacks[i],
						"Unrecognized Callback");
			}
		}
	}
}