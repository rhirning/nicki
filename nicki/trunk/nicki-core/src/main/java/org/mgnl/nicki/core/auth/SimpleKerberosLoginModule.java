package org.mgnl.nicki.core.auth;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import org.apache.commons.lang.StringUtils;
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
		LOG.debug("Using " + getClass().getCanonicalName());
		String userId = AppContext.getUserId();
		if (StringUtils.isBlank(userId)) {
			return false;
		}
		if (StringUtils.contains(userId, "@")) {
			userId = StringUtils.substringBefore(userId, "@");
		}
		DynamicObject user = loadUser(userId);
		if (user == null) {
			LOG.debug("Invalid user " + userId);
			return false;
		}
		try {
			setContext(user.getContext().getTarget().getSystemContext(user));
		} catch (InvalidPrincipalException e) {
			LOG.debug("Error validation context for user " + userId, e);
			return false;
		}

		// TODO: separate context / loginContext
		DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(userId,
				getContext(), getContext());
		setPrincipal(dynamicObjectPrincipal);
		setSucceeded(true);
		
		LOG.debug("user ok " + userId);
		return true;
	}

}
