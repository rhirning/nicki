/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.auth;

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
