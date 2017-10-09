
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


import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class NickiLoginModule implements LoginModule {
	private static final Logger LOG = LoggerFactory.getLogger(NickiLoginModule.class);
	
	// initial state
	private Subject subject;
	private CallbackHandler callbackHandler;
	private Map<String, ?> sharedState;
	private Map<String, ?> options;

	// configurable option
	private boolean debug;
	private String targetName;
	private boolean useSystemContext;

	// the authentication status
	private boolean succeeded = false;
	private boolean commitSucceeded = false;
	
	// Principal	
	private NickiContext loginContext;
	private DynamicObjectPrincipal principal;
	
	protected NickiLoginModule() {
		
	}

	@Override
	public abstract boolean login() throws LoginException;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {

		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;

		// initialize any configured options
		debug = "true".equalsIgnoreCase((String) options.get("debug"));
		targetName = StringUtils.stripToNull((String) options.get("target"));
		useSystemContext = DataHelper.booleanOf(StringUtils.stripToNull((String) options.get("useSystemContext")));
	}
	
	protected NickiContext login(NickiPrincipal principal) throws InvalidPrincipalException {
		Target target;
		if (targetName != null) {
			target = TargetFactory.getTarget(targetName);
		} else {
			target = TargetFactory.getDefaultTarget();
		}
		DynamicObject user = target.login(principal);
		if (user != null) {
			if (isUseSystemContext()) {
				return AppContext.getSystemContext(target, user.getPath(), principal.getPassword());
			} else {
				return target.getNamedUserContext(user, principal.getPassword());
			}
		}
		throw new InvalidPrincipalException();
	}
	
	public NickiContext login(String name, byte[] credential) {
		try {
			NickiPrincipal principal = new NickiPrincipal(name, new String(credential));
			if (principal != null) {
				DynamicObject user = getTarget().login(principal);
				if (user != null) {
					LOG.debug("Login sucessful, user=" + user);
					return getTarget().getNamedUserContext(user, new String(credential));
				}
			}
		} catch (Exception e) {
			LOG.debug("Login failed, user=" + name, e);
		}
		return null;
	}

	private Target getTarget() {
		if (targetName != null) {
			return TargetFactory.getTarget(targetName);
		} else {
			return TargetFactory.getDefaultTarget();
		}
	}

	protected DynamicObject loadUser(String userId) {
		if (StringUtils.contains(userId, "@")) {
			userId = StringUtils.substringBefore(userId, "@");
		}
		List<? extends DynamicObject> list = null;
		try {
			list = AppContext.getSystemContext().loadObjects(Config.getString("nicki.users.basedn"), "cn=" + userId);
		} catch (InvalidPrincipalException e) {
			LOG.error("Invalid SystemContext", e);
		}
		
		if (list != null && list.size() == 1) {
			LOG.info("login: loadObjects successful");
			return list.get(0);
		} else {
			LOG.info("login: loadObjects not successful");
			LOG.debug("Loading Objects not successful: " 
					+ ((list == null)?"null":"size=" + list.size()));
			return null;
		}
	}

	@Override
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			// add a Principal (authenticated identity)
			// to the Subject

			if (!subject.getPrincipals().contains(this.principal)) {
				subject.getPrincipals().add(this.principal);
			}

			commitSucceeded = true;
			return true;
		}
	}

	@Override
	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			// login succeeded but overall authentication failed
			succeeded = false;
			principal = null;
			loginContext = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(principal);
		succeeded = false;
		succeeded = commitSucceeded;
		principal = null;
		return true;
	}

	protected CallbackHandler getCallbackHandler() {
		return callbackHandler;
	}

	protected void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}

	protected Map<String, ?> getSharedState() {
		return sharedState;
	}

	protected Map<String, ?> getOptions() {
		return options;
	}

	protected boolean isDebug() {
		return debug;
	}

	protected NickiContext getLoginContext() {
		return loginContext;
	}

	protected void setLoginContext(NickiContext loginContext) {
		this.loginContext = loginContext;
	}

	protected String getTargetName() {
		return targetName;
	}

	protected boolean isUseSystemContext() {
		return useSystemContext;
	}

	public DynamicObjectPrincipal getDynamicObjectPrincipal() {
		return principal;
	}

	public void setPrincipal(DynamicObjectPrincipal dynamicObjectPrincipal) {
		if (this.subject == null) {
			this.subject = new Subject();
			LOG.debug("create new Subject");
		} else {
			LOG.debug("Subject exists");
		}
		this.subject.getPrincipals().add(dynamicObjectPrincipal);
		this.principal = dynamicObjectPrincipal;
	}

	public Subject getSubject() {
		LOG.debug("getSubject: " + subject);
		return subject;
	}

}
