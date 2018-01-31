
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


import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.DynamicObjectPrincipal;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiLoginModule;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingLoginModule extends NickiLoginModule {
	static final Logger LOG = LoggerFactory.getLogger(MappingLoginModule.class);
    



	@Override
	public boolean login() throws LoginException {
		LOG.debug("Using " + getClass().getCanonicalName());
		HttpServletRequest request = (HttpServletRequest) AppContext.getRequest();
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			String authenticatedUser = (String) req.getSession().getAttribute(SESSION_USER);
			String header = req.getHeader(AUTHZ_HEADER);;
			if (StringUtils.isBlank(header)) {
				header = (String) req.getSession().getAttribute(SESSION_AUTH_HEADER);
				if (StringUtils.isNotBlank(header)) {
					LOG.debug("Authorization header: " + header);
				}
			}
			DynamicObject user = null;
			byte credentials[] = null;
			if (StringUtils.isNotBlank(authenticatedUser)) {
				LOG.debug("Authenticated user: " + authenticatedUser);
				user = loadUser(authenticatedUser);
			} else {
				if (StringUtils.isBlank(header)) {
					user = null;
					LOG.debug("authorization header was missing/null");
					return false;
				}
			}
			if (user != null) {
				NickiContext loginContext;
				NickiContext nickiContext;
				if (isUseSystemContext()) {
					try {
						loginContext = getLoginTarget().getNamedUserContext(user,
								credentials!=null?new String(credentials):"unknown");
						nickiContext = getTarget().getSystemContext(user);
					} catch (InvalidPrincipalException e) {
						LOG.debug("login not successful: " + user, e);
						return false;
					}
				} else {
					loginContext = login(user.getName(), credentials);
					nickiContext = loginContext;
				}
				if (loginContext != null && nickiContext != null) {
					setLoginContext(loginContext);
					DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(user.getName(),
							loginContext, nickiContext);
					setPrincipal(dynamicObjectPrincipal);
					setSucceeded(true);
					LOG.debug("login successful:  " + user);
					return true;
				} else {
					LOG.debug("login not successful: " + user);
				}
			} else {
				LOG.debug("no valid user");
			}
		}
		return false;
	}

}
