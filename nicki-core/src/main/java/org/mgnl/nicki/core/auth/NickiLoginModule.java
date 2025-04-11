
package org.mgnl.nicki.core.auth;

import java.io.IOException;

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
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.DynamicObject;
import lombok.extern.slf4j.Slf4j;
// TODO: Auto-generated Javadoc

/**
 * LoginModule which handles most of the necessary tasks.
 */
@Slf4j
public abstract class NickiLoginModule implements LoginModule {
	
    /** The Constant AUTHZ_HEADER. */
    public static final String AUTHZ_HEADER = "Authorization";
    
    /** The Constant SESSION_USER. */
    public static final String SESSION_USER = "NICKI_SESSION_USER";
    
    /** The Constant SESSION_AUTH_HEADER. */
    public static final String SESSION_AUTH_HEADER = "NICKI_SESSION_AUTH_HEADER";
	
	/** The subject. */
	// initial state
	private Subject subject;
	
	/** The callback handler. */
	private CallbackHandler callbackHandler;
	
	/** The shared state. */
	private Map<String, ?> sharedState;
	
	/** The options. */
	private Map<String, ?> options;

	/** The debug. */
	// configurable option
	private boolean debug;
	
	/** The use system context. */
	private boolean useSystemContext;

	/** The succeeded. */
	// the authentication status
	private boolean succeeded = false;
	
	/** The commit succeeded. */
	private boolean commitSucceeded = false;
	
	/** The login context. */
	// Principal	
	private NickiContext loginContext;
	
	/** The principal. */
	private DynamicObjectPrincipal principal;
	
	/**
	 * Instantiates a new nicki login module.
	 */
	protected NickiLoginModule() {
		
	}

	/**
	 * Login.
	 *
	 * @return true, if successful
	 * @throws LoginException the login exception
	 */
	@Override
	public abstract boolean login() throws LoginException;

	/**
	 * Initialize.
	 *
	 * @param subject the subject
	 * @param callbackHandler the callback handler
	 * @param sharedState the shared state
	 * @param options the options
	 */
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {

		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;

		// initialize any configured options
		debug = "true".equalsIgnoreCase((String) options.get("debug"));
		useSystemContext = DataHelper.booleanOf(StringUtils.stripToNull((String) options.get("useSystemContext")));
	}
	
	/**
	 * Login.
	 *
	 * @param principal the principal
	 * @return the nicki context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	protected NickiContext login(NickiPrincipal principal) throws InvalidPrincipalException {

		DynamicObject user = getLoginTarget().login(principal);
		if (user != null) {

			Target target = getTarget();
			if (isUseSystemContext()) {
				return AppContext.getSystemContext(target, user.getPath(), principal.getPassword());
			} else {
				return target.getNamedUserContext(user, principal.getPassword());
			}
		}
		throw new InvalidPrincipalException();
	}
	
	/**
	 * Login.
	 *
	 * @param name the name
	 * @param credential the credential
	 * @return the nicki context
	 */
	public NickiContext login(String name, byte[] credential) {
		try {
			NickiPrincipal principal = new NickiPrincipal(name, new String(credential));
			if (principal != null) {
				DynamicObject user = getLoginTarget().login(principal);
				if (user != null) {
					log.debug("Login sucessful, user=" + user);
					return getTarget().getNamedUserContext(user, new String(credential));
				}
			}
		} catch (Exception e) {
			log.debug("Login failed, user=" + name, e);
		}
		return null;
	}

	/**
	 * Gets the login target.
	 *
	 * @return the login target
	 */
	public Target getLoginTarget() {
		if (getCallbackHandler() != null) {
			LoginTargetCallback[] callbacks = new LoginTargetCallback[1];
			callbacks[0] = new LoginTargetCallback();
			try {
				getCallbackHandler().handle(callbacks);
				return TargetFactory.getTarget(callbacks[0].getLoginTarget());
			} catch (IOException | UnsupportedCallbackException e) {
				log.error("Error with CallbackHandler", e);
			}
		}
		return TargetFactory.getDefaultTarget();
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public Target getTarget() {
		if (getCallbackHandler() != null) {
			TargetCallback[] callbacks = new TargetCallback[1];
			callbacks[0] = new TargetCallback();
			try {
				getCallbackHandler().handle(callbacks);
				return TargetFactory.getTarget(callbacks[0].getTarget());
			} catch (IOException | UnsupportedCallbackException e) {
				log.error("Error with CallbackHandler", e);
			}
		}
		return TargetFactory.getDefaultTarget();
	}

	/**
	 * Load user.
	 *
	 * @param userId the user id
	 * @return the dynamic object
	 */
	protected DynamicObject loadUser(String userId) {
		if (StringUtils.contains(userId, "@")) {
			userId = StringUtils.substringBefore(userId, "@");
		}
		List<? extends DynamicObject> list = null;
		try {
			Target loginTarget = getLoginTarget();
			list = AppContext.getSystemContext(loginTarget.getName()).loadObjects(loginTarget.getBaseDn(), "cn=" + userId);
		} catch (InvalidPrincipalException e) {
			log.error("Invalid SystemContext", e);
		}
		
		if (list != null && list.size() == 1) {
			log.info("login: loadObjects successful");
			return list.get(0);
		} else {
			log.info("login: loadObjects not successful");
			log.debug("Loading Objects not successful: " 
					+ ((list == null)?"null":"size=" + list.size()));
			return null;
		}
	}

	/**
	 * Commit.
	 *
	 * @return true, if successful
	 * @throws LoginException the login exception
	 */
	@Override
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			// add a Principal (authenticated identity)
			// to the Subject

			if (this.principal != null && !subject.getPrincipals().contains(this.principal)) {
				subject.getPrincipals().add(this.principal);
			}

			commitSucceeded = true;
			return true;
		}
	}

	/**
	 * Abort.
	 *
	 * @return true, if successful
	 * @throws LoginException the login exception
	 */
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

	/**
	 * Logout.
	 *
	 * @return true, if successful
	 * @throws LoginException the login exception
	 */
	@Override
	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(principal);
		succeeded = false;
		succeeded = commitSucceeded;
		principal = null;
		return true;
	}

	/**
	 * Gets the callback handler.
	 *
	 * @return the callback handler
	 */
	protected CallbackHandler getCallbackHandler() {
		return callbackHandler;
	}

	/**
	 * Sets the succeeded.
	 *
	 * @param succeeded the new succeeded
	 */
	protected void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}

	/**
	 * Gets the shared state.
	 *
	 * @return the shared state
	 */
	protected Map<String, ?> getSharedState() {
		return sharedState;
	}

	/**
	 * Gets the options.
	 *
	 * @return the options
	 */
	protected Map<String, ?> getOptions() {
		return options;
	}

	/**
	 * Checks if is debug.
	 *
	 * @return true, if is debug
	 */
	protected boolean isDebug() {
		return debug;
	}

	/**
	 * Gets the login context.
	 *
	 * @return the login context
	 */
	protected NickiContext getLoginContext() {
		return loginContext;
	}

	/**
	 * Sets the login context.
	 *
	 * @param loginContext the new login context
	 */
	protected void setLoginContext(NickiContext loginContext) {
		this.loginContext = loginContext;
	}

	/**
	 * Checks if is use system context.
	 *
	 * @return true, if is use system context
	 */
	protected boolean isUseSystemContext() {
		return useSystemContext;
	}

	/**
	 * Gets the dynamic object principal.
	 *
	 * @return the dynamic object principal
	 */
	public DynamicObjectPrincipal getDynamicObjectPrincipal() {
		return principal;
	}

	/**
	 * Sets the principal.
	 *
	 * @param dynamicObjectPrincipal the new principal
	 */
	public void setPrincipal(DynamicObjectPrincipal dynamicObjectPrincipal) {
		if (this.subject == null) {
			this.subject = new Subject();
			log.debug("create new Subject");
		} else {
			log.debug("Subject exists");
		}
		this.subject.getPrincipals().add(dynamicObjectPrincipal);
		this.principal = dynamicObjectPrincipal;
	}

	/**
	 * Gets the subject.
	 *
	 * @return the subject
	 */
	public Subject getSubject() {
		log.debug("getSubject: " + subject);
		return subject;
	}

	/**
	 * Used by the BASIC Auth mechanism for establishing a LoginContext to
	 * authenticate a client/caller/request.
	 * 
	 * @param username
	 *            client username
	 * @param password
	 *            client password
	 * @return CallbackHandler to be used for establishing a LoginContext
	 */
	public static CallbackHandler getUsernamePasswordHandler(final String username, final String password) {

		log.debug("username=" + username + "; password=" + password.hashCode());

		final CallbackHandler handler = new CallbackHandler() {
			public void handle(final Callback[] callback) {
				for (int i = 0; i < callback.length; i++) {
					if (callback[i] instanceof NameCallback) {
						final NameCallback nameCallback = (NameCallback) callback[i];
						nameCallback.setName(username);
					} else if (callback[i] instanceof PasswordCallback) {
						final PasswordCallback passCallback = (PasswordCallback) callback[i];
						passCallback.setPassword(password.toCharArray());
					} else {
						log.debug("Unsupported Callback i=" + i + "; class=" + callback[i].getClass().getName());
					}
				}
			}
		};

		return handler;
	}

}
