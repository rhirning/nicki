package org.mgnl.nicki.novell.userapp.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.auth.NickiLoginCallbackHandler;
import org.mgnl.nicki.ldap.auth.NickiLoginModule;

public class PortalLoginModule extends NickiLoginModule implements LoginModule {

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
