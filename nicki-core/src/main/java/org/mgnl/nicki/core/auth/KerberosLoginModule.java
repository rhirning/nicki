
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


import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KerberosLoginModule extends NickiLoginModule {
	public static final String PREAUTH_USER_CONF = "spnego.preauth.username";
    public static final String PREAUTH_PASSWORD_CONF = "spnego.preauth.password";
    public static final String SERVER_LOGIN_MODULE_CONF = "spnego.server.loginmodule";
    
	/** GSSContext is not thread-safe. */
	private static final Lock LOCK = new ReentrantLock();
	
	private KerberosConfig kerberosConfig = new KerberosConfig();

	/** GSS-API mechanism "1.3.6.1.5.5.2". */
	private static final Oid SPNEGO_OID = getOid();

	/** Default GSSManager. */
	private static final GSSManager MANAGER = GSSManager.getInstance();
	/** Zero length byte array. */
	private static final transient byte[] EMPTY_BYTE_ARRAY = new byte[0];
	/**
	 * HTTP Request Header <b>Authorization</b>.
	 * 
	 * <p>
	 * Clients should send this header where the value is the authentication
	 * token(s).
	 * </p>
	 */
	public static final String AUTHZ_HEADER = "Authorization";

	/**
	 * HTTP Response Header <b>Negotiate</b>.
	 * 
	 * <p>
	 * The filter will set this as the value for the "WWW-Authenticate" header.
	 * Note that the filter may also add another header with a value of "Basic"
	 * (if allowed by the web.xml file).
	 * </p>
	 */
	public static final String NEGOTIATE_HEADER = "Negotiate";
    public static final String SESSION_AUTH_HEADER = "NICKI_SESSION_AUTH_HEADER";

	@Override
	public boolean login() throws LoginException {
		log.debug("Using " + getClass().getCanonicalName());
		HttpServletRequest request = (HttpServletRequest) AppContext.getRequest();
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			String authenticatedUser = (String) req.getSession().getAttribute(SESSION_USER);
			String header = req.getHeader(AUTHZ_HEADER);;
			if (StringUtils.isBlank(header)) {
				header = (String) req.getSession().getAttribute(SESSION_AUTH_HEADER);
				if (StringUtils.isNotBlank(header)) {
					log.debug("Authorization header: " + header);
				}
			}
			DynamicObject user = null;
			byte credentials[] = null;
			if (StringUtils.isNotBlank(authenticatedUser)) {
				log.debug("Authenticated user: " + authenticatedUser);
				user = loadUser(authenticatedUser);
			} else {
				if (StringUtils.isBlank(header)) {
					user = null;
					log.debug("authorization header was missing/null");
					return false;
				} else if (header.startsWith(NEGOTIATE_HEADER)) {
					final String negotiateHeader = header.substring(NEGOTIATE_HEADER.length() + 1);
					final SpnegoPrincipal principal;
					final byte[] gss = decodeToken(negotiateHeader);
		
					if (0 == gss.length) {
						log.debug("GSS data was NULL.");
						return false;
					}
		
					GSSContext context = null;
			        GSSCredential delegCred = null;
					try {
						byte[] token = null;
		
						LOCK.lock();
						try {
							context = MANAGER.createContext(kerberosConfig.getServerCredentials());
							token = context.acceptSecContext(gss, 0, gss.length);
		
							if (null == token) {
								log.debug("Token was NULL.");
								return false;
							}
				            
				            if (context.getCredDelegState()) {
				                delegCred = context.getDelegCred();
				            }
				            if (context.getSrcName() != null) {
								String principalId = context.getSrcName().toString();
								principal = new SpnegoPrincipal(principalId, KerberosPrincipal.KRB_NT_PRINCIPAL, gss, delegCred);
								log.debug("principal=" + principal);
								credentials = principal.getCredential();
								user = loadUser(principal.getName());
				            }
							
						} catch (GSSException e) {
							log.error("Error with token", e);
						} finally {
							LOCK.unlock();
						}
		
					} finally {
						if (null != context) {
							LOCK.lock();
							try {
								context.dispose();
							} catch (GSSException e) {
								log.error("Error disposing context", e);
							} finally {
								LOCK.unlock();
							}
						}
					}
				}
			}
			if (user != null) {
				NickiContext loginContext;
				NickiContext nickiContext;
				if (isUseSystemContext()) {
					try {
						loginContext = user.getContext().getTarget().getNamedUserContext(user,
								credentials!=null?new String(credentials):"unknown");
						nickiContext = user.getContext().getTarget().getSystemContext(user);
					} catch (InvalidPrincipalException e) {
						log.debug("login not successful: " + user, e);
						return false;
					}
				} else {
					if (credentials == null || credentials.length == 0) {
						credentials = "unknown".getBytes();
					}
					loginContext = login(user.getName(), credentials);
					nickiContext = loginContext;
				}
				if (loginContext != null && nickiContext != null) {
					setLoginContext(loginContext);
					DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(user.getName(),
							loginContext, nickiContext);
					setPrincipal(dynamicObjectPrincipal);
					setSucceeded(true);
					log.debug("login successful:  " + user);
					return true;
				} else {
					log.debug("login not successful: " + user);
				}
			} else {
				log.debug("no valid user");
			}
		}
		return false;
	}
	
	/**
	 * Returns a copy of byte[].
	 * 
	 * @return copy of token
	 */
	private byte[] decodeToken(String token) {
		return (null == token) ? EMPTY_BYTE_ARRAY : Base64.decodeBase64(token);
	}

	/**
	 * Returns the {@link GSSCredential} the server uses for pre-authentication.
	 * 
	 * @param subject
	 *            account server uses for pre-authentication
	 * @return credential that allows server to authenticate clients
	 * @throws PrivilegedActionException
	 */
	static GSSCredential getServerCredential(final Subject subject) throws PrivilegedActionException {

		final PrivilegedExceptionAction<GSSCredential> action = new PrivilegedExceptionAction<GSSCredential>() {
			public GSSCredential run() throws GSSException {
				return MANAGER.createCredential(null, GSSCredential.INDEFINITE_LIFETIME, SPNEGO_OID,
						GSSCredential.ACCEPT_ONLY);
			}
		};
		return Subject.doAs(subject, action);
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

		log.debug("preauth: username=" + username + "; password=" + password.hashCode());

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

	/**
	 * Returns the Universal Object Identifier representation of the SPNEGO
	 * mechanism.
	 * 
	 * @return Object Identifier of the GSS-API mechanism
	 */
	private static Oid getOid() {
		Oid oid = null;
		try {
			oid = new Oid("1.3.6.1.5.5.2");
		} catch (GSSException gsse) {
			log.error("Unable to create OID 1.3.6.1.5.5.2 !", gsse);
		}
		return oid;
	}
	
	public class KerberosConfig {
		private boolean init;
		
		/** Credentials server uses for authenticating requests. */
		private transient GSSCredential serverCredentials;

		public KerberosConfig() {
			synchronized (AUTHZ_HEADER) {
				if (!init) {
					init();
					init = true;
				}
			}
		}

		private synchronized void init() {
			String preauthUsername = Config.getString(PREAUTH_USER_CONF);
			String preauthPassword = Config.getString(PREAUTH_PASSWORD_CONF);
			String serverLoginModule = Config.getString(SERVER_LOGIN_MODULE_CONF, "spnego-server");

			final CallbackHandler handler = getUsernamePasswordHandler(preauthUsername, preauthPassword);

			try {
				LoginContext loginContext = new LoginContext(serverLoginModule, handler);

				loginContext.login();

				this.serverCredentials = getServerCredential(loginContext.getSubject());
			} catch (LoginException | PrivilegedActionException e) {
				log.error("Error initializing KerberosLoginModule", e);
			}
		}

		public GSSCredential getServerCredentials() {
			return serverCredentials;
		}		
	}

}
