package org.mgnl.nicki.core.auth;

import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.mgnl.nicki.core.context.AppContext;

public class OpenLoginModule extends NickiLoginModule implements LoginModule {

	@Override
	public boolean login() throws LoginException {
		try {
			NickiPrincipal nickiPrincipal = new NickiPrincipal("anonymous", "none");
			setPrincipal(new DynamicObjectPrincipal(nickiPrincipal, AppContext.getSystemContext(), AppContext.getSystemContext()));
			//getSubject().getPrincipals().add(principal);
			setSucceeded(true);
			return true;
		} catch (InvalidPrincipalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
