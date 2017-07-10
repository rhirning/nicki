package org.mgnl.nicki.core.auth;

import javax.security.auth.login.LoginException;
import org.mgnl.nicki.core.auth.DynamicObjectPrincipal;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiLoginModule;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleKerberosLoginModule extends NickiLoginModule {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleKerberosLoginModule.class);

	@Override
	public boolean login() throws LoginException {
		LOG.debug("Using " + getClass().getCanonicalName() + ", ThreadId=" + Thread.currentThread().getId());
		DynamicObject user = AppContext.getUser();
		if (user == null) {
			return false;
		}
		try {
			setContext(user.getContext().getTarget().getSystemContext(user));
		} catch (InvalidPrincipalException e) {
			LOG.debug("Error validation context for user " + user.getDisplayName(), e);
			return false;
		}

		// TODO: separate context / loginContext
		DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(user.getName(),
				getContext(), getContext());
		setPrincipal(dynamicObjectPrincipal);
		setSucceeded(true);
		
		LOG.debug("user ok " + user.getName());
		return true;
	}

}
