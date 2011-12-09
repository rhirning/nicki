/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
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
