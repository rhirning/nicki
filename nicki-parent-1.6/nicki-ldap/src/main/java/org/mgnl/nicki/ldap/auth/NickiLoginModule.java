package org.mgnl.nicki.ldap.auth;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;


public abstract class NickiLoginModule implements LoginModule {

	// initial state
	private Subject subject;
	private CallbackHandler callbackHandler;
	private Map<String, ?> sharedState;
	private Map<String, ?> options;

	// configurable option
	private boolean debug = false;

	// the authentication status
	private boolean succeeded = false;
	private boolean commitSucceeded = false;

	// username and password
	private String username;
	private String password;
	
	// Principal
	private NickiPrincipal userPrincipal;
	
	protected NickiLoginModule() {
		
	}

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
	}


	@Override
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			// add a Principal (authenticated identity)
			// to the Subject

			// assume the user we authenticated is the SamplePrincipal
			try {
				userPrincipal = new NickiPrincipal(username, password);
			} catch (InvalidPrincipalException e) {
				throw new LoginException(e.getMessage());
			}
			if (!subject.getPrincipals().contains(userPrincipal)) {
				subject.getPrincipals().add(userPrincipal);
			}

			// in any case, clean out state
			username = null;

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
			username = null;
			userPrincipal = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(userPrincipal);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		userPrincipal = null;
		return true;
	}

	protected CallbackHandler getCallbackHandler() {
		return callbackHandler;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	protected void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}

	protected void setPassword(String password) {
		this.password = password;
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

}
