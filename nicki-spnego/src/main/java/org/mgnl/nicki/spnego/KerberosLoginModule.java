
package org.mgnl.nicki.spnego;

/*-
 * #%L
 * nicki-spnego
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
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.mgnl.nicki.core.auth.DynamicObjectPrincipal;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiLoginModule;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KerberosLoginModule extends NickiLoginModule {
	private static final Logger LOG = LoggerFactory.getLogger(KerberosLoginModule.class);
	/** GSSContext is not thread-safe. */
	private static final Lock LOCK = new ReentrantLock();
	/** Login Context server uses for pre-authentication. */
	private transient LoginContext loginContext;

	/** Credentials server uses for authenticating requests. */
	private transient GSSCredential serverCredentials;

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

	@Override
	public boolean login() throws LoginException {
		HttpServletRequest request = (HttpServletRequest) AppContext.getRequest();
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			String header = req.getHeader(AUTHZ_HEADER);
			if (StringUtils.isBlank(header)) {
				LOG.debug("authorization header was missing/null");
				return false;
			} else if (header.startsWith(NEGOTIATE_HEADER)) {
				final String negotiateHeader = header.substring(NEGOTIATE_HEADER.length() + 1);
				final String principal;
				final byte[] gss = decodeToken(negotiateHeader);

				if (0 == gss.length) {
					LOG.debug("GSS data was NULL.");
					return false;
				}

				GSSContext context = null;
				try {
					byte[] token = null;

					LOCK.lock();
					try {
						context = MANAGER.createContext(this.serverCredentials);
						token = context.acceptSecContext(gss, 0, gss.length);

						if (null == token) {
							LOG.debug("Token was NULL.");
							return false;
						}
						principal = context.getSrcName().toString();
						DynamicObject user = loadUser(principal);
						setContext(user.getContext().getTarget().getSystemContext(user));

						// TODO: separate context / loginContext
						DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(principal,
								getContext(), getContext());
						setPrincipal(dynamicObjectPrincipal);
						setSucceeded(true);
					} catch (GSSException e) {
						LOG.error("Error with token", e);
					} catch (InvalidPrincipalException e) {
						LOG.error("Error with SystemCredentials", e);
					} finally {
						LOCK.unlock();
					}

				} finally {
					if (null != context) {
						LOCK.lock();
						try {
							context.dispose();
						} catch (GSSException e) {
							LOG.error("Error disposing context", e);
						} finally {
							LOCK.unlock();
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns a copy of byte[].
	 * 
	 * @return copy of token
	 */
	byte[] decodeToken(String token) {
		return (null == token) ? EMPTY_BYTE_ARRAY : Base64.decodeBase64(token);
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		String preauthUsername = Config.getString("nicki.spnego.kerberos.preauth.username");
		String preauthPassword = Config.getString("nicki.spnego.kerberos.preauth.password");
		String serverLoginModule = Config.getString("nicki.spnego.kerberos.server.loginmodule");

		final CallbackHandler handler = getUsernamePasswordHandler(preauthUsername, preauthPassword);

		try {
			this.loginContext = new LoginContext(serverLoginModule, handler);

			this.loginContext.login();

			this.serverCredentials = getServerCredential(this.loginContext.getSubject());
		} catch (LoginException | PrivilegedActionException e) {
			LOG.error("Error initializing", e);
		}
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

		LOG.debug("username=" + username + "; password=" + password.hashCode());

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
						LOG.debug("Unsupported Callback i=" + i + "; class=" + callback[i].getClass().getName());
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
			LOG.error("Unable to create OID 1.3.6.1.5.5.2 !", gsse);
		}
		return oid;
	}

}
