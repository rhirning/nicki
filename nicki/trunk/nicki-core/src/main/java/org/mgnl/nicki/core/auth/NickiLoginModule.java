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
package org.mgnl.nicki.core.auth;

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
	private NickiContext context;
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

	protected DynamicObject loadUser(String userId) {
		List<? extends DynamicObject> list = null;
		try {
			list = AppContext.getSystemContext().loadObjects(Config.getProperty("nicki.users.basedn"), "cn=" + userId);
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
			context = null;
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

	protected NickiContext getContext() {
		return context;
	}

	protected void setContext(NickiContext nickiContext) {
		this.context = nickiContext;
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
			this.subject.getPrincipals().add(dynamicObjectPrincipal);
		}
		this.principal = dynamicObjectPrincipal;
	}

	public Subject getSubject() {
		return subject;
	}

}
