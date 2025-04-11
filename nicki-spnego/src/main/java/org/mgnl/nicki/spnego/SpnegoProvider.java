
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


import java.io.IOException;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.mgnl.nicki.spnego.SpnegoHttpFilter.Constants;

import lombok.extern.slf4j.Slf4j;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

// TODO: Auto-generated Javadoc
/**
 * This is a Utility Class that can be used for finer grained control 
 * over message integrity, confidentiality and mutual authentication.
 * 
 * <p>
 * This Class is exposed for developers who want to implement a custom 
 * HTTP client.
 * </p>
 * 
 * <p>
 * Take a look at the {@link SpnegoHttpURLConnection} class and the 
 * {@link SpnegoHttpFilter} class before attempting to implement your 
 * own HTTP client.
 * </p>
 * 
 * <p>For more example usage, see the documentation at 
 * <a href="http://spnego.sourceforge.net" target="_blank">http://spnego.sourceforge.net</a>
 * </p>
 * 
 * @author Darwin V. Felix
 * 
 */
@Slf4j
public final class SpnegoProvider {

    /** Factory for GSS-API mechanism. */
    private static final GSSManager MANAGER = GSSManager.getInstance();

    /** GSS-API mechanism "1.3.6.1.5.5.2". */
    private static final Oid SPNEGO_OID = SpnegoProvider.getOid();

    /**
     * Instantiates a new spnego provider.
     */
    /*
     * This is a utility class (not a Singleton).
     */
    private SpnegoProvider() {
        // default private
    }

    /**
     * Returns the {@link SpnegoAuthScheme} mechanism used to authenticate 
     * the request. 
     * 
     * <p>
     * This method may return null in which case you must check the HTTP 
     * Status Code to determine if additional processing is required.
     * <br />
     * For example, if req. did not contain the Constants.AUTHZ_HEADER, 
     * the HTTP Status Code SC_UNAUTHORIZED will be set and the client must 
     * send authentication information on the next request.
     * </p>
     *
     * @param req servlet request
     * @param resp servlet response
     * @param basicSupported pass true to offer/allow BASIC Authentication
     * @param promptIfNtlm pass true ntlm request should be downgraded
     * @param realm should be the realm the server used to pre-authenticate
     * @return null if negotiation needs to continue or failed
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static SpnegoAuthScheme negotiate(
        final HttpServletRequest req, final SpnegoHttpServletResponse resp
        , final boolean basicSupported, final boolean promptIfNtlm
        , final String realm) throws IOException {

        final SpnegoAuthScheme scheme = SpnegoProvider.getAuthScheme(
                req.getHeader(Constants.AUTHZ_HEADER));
        
        if (null == scheme || scheme.getToken().length == 0) {
            log.debug("Header Token was NULL");
        	if (req.getSession().getAttribute(SpnegoHttpFilter.SESSION_AUTH_REQUEST) == null) {
	            resp.setHeader(Constants.AUTHN_HEADER, Constants.NEGOTIATE_HEADER);
	            //req.getSession(true).setAttribute(SESSION_AUTH_REQUEST, "1");
	
	            if (basicSupported) {
	                resp.addHeader(Constants.AUTHN_HEADER,
	                    Constants.BASIC_HEADER + " realm=\"" + realm + '\"');
	            } else {
	                log.debug("Basic NOT offered: Not Enabled or SSL Required.");
	            }
	
	            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED, true);
	            
	            return null;
        	} else {
        		// Not authenticated, but no further try
        		return SpnegoAuthScheme.getNotSupportedScheme();
        	}
        }
        
        // assert
        if (scheme.isNtlmToken()) {
            log.warn("Downgrade NTLM request to Basic Auth.");

            if (resp.isStatusSet()) {
                throw new IllegalStateException("HTTP Status already set.");
            }

            if (basicSupported && promptIfNtlm) {
                resp.setHeader(Constants.AUTHN_HEADER,
                        Constants.BASIC_HEADER + " realm=\"" + realm + '\"');
            } else {
                // TODO : decode/decrypt NTLM token and return a new SpnegoAuthScheme
                // of type "Basic" where the token value is a base64 encoded
                // username + ":" + password string
                throw new UnsupportedOperationException("NTLM specified. Downgraded to " 
                        + "Basic Auth (and/or SSL) but downgrade not supported.");
            }
            
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED, true);
            
            return null;
        }
        
        return scheme;
    }
    
    /**
     * Returns the GSS-API interface for creating a security context.
     *
     * @param subject the person to be authenticated
     * @return GSSCredential to be used for creating a security context.
     * @throws PrivilegedActionException the privileged action exception
     */
    public static GSSCredential getClientCredential(final Subject subject)
        throws PrivilegedActionException {

        final PrivilegedExceptionAction<GSSCredential> action = 
            new PrivilegedExceptionAction<GSSCredential>() {
                public GSSCredential run() throws GSSException {
                    return MANAGER.createCredential(
                        null
                        , GSSCredential.DEFAULT_LIFETIME
                        , SpnegoProvider.SPNEGO_OID
                        , GSSCredential.INITIATE_ONLY);
                } 
            };
        
        return Subject.doAs(subject, action);
    }
    
    /**
     * Returns a GSSContext to be used by custom clients to set 
     * data integrity requirements, confidentiality and if mutual 
     * authentication is required.
     *
     * @param creds credentials of the person to be authenticated
     * @param url HTTP address of server (used for constructing a {@link GSSName}).
     * @return GSSContext
     * @throws GSSException the GSS exception
     */
    public static GSSContext getGSSContext(final GSSCredential creds, final URL url) 
        throws GSSException {
        
        return MANAGER.createContext(SpnegoProvider.getServerName(url)
                , SpnegoProvider.SPNEGO_OID
                , creds
                , GSSContext.DEFAULT_LIFETIME);
    }
    
    /**
     * Returns the {@link SpnegoAuthScheme} or null if header is missing.
     * 
     * <p>
     * Throws UnsupportedOperationException if header is NOT Negotiate 
     * or Basic. 
     * </p>
     * 
     * @param header ex. Negotiate or Basic
     * @return null if header missing/null else the auth scheme
     */
    public static SpnegoAuthScheme getAuthScheme(final String header) {

        if (null == header || header.isEmpty()) {
            log.debug("authorization header was missing/null");
            return null;
            
        } else if (header.startsWith(Constants.NEGOTIATE_HEADER)) {
            final String token = header.substring(Constants.NEGOTIATE_HEADER.length() + 1);
            log.debug("Negotiate Header: " + token);
            return new SpnegoAuthScheme(Constants.NEGOTIATE_HEADER, token);
            
        } else if (header.startsWith(Constants.BASIC_HEADER)) {
            final String token = header.substring(Constants.BASIC_HEADER.length() + 1);
            log.debug("Basic Header: " + token);
            return new SpnegoAuthScheme(Constants.BASIC_HEADER, token);
            
        } else {
            throw new UnsupportedOperationException("Negotiate or Basic Only:" + header);
        }
    }
    
    /**
     * Returns the Universal Object Identifier representation of 
     * the SPNEGO mechanism.
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

    /**
     * Returns the {@link GSSCredential} the server uses for pre-authentication.
     *
     * @param subject account server uses for pre-authentication
     * @return credential that allows server to authenticate clients
     * @throws PrivilegedActionException the privileged action exception
     */
    static GSSCredential getServerCredential(final Subject subject)
        throws PrivilegedActionException {
        
        final PrivilegedExceptionAction<GSSCredential> action = 
            new PrivilegedExceptionAction<GSSCredential>() {
                public GSSCredential run() throws GSSException {
                    return MANAGER.createCredential(
                        null
                        , GSSCredential.INDEFINITE_LIFETIME
                        , SpnegoProvider.SPNEGO_OID
                        , GSSCredential.ACCEPT_ONLY);
                } 
            };
        return Subject.doAs(subject, action);
    }

    /**
     * Returns the {@link GSSName} constructed out of the passed-in 
     * URL object.
     *
     * @param url HTTP address of server
     * @return GSSName of URL.
     * @throws GSSException the GSS exception
     */
    private static GSSName getServerName(final URL url) throws GSSException {
        return MANAGER.createName("HTTP@" + url.getHost(),
            GSSName.NT_HOSTBASED_SERVICE, SpnegoProvider.SPNEGO_OID);
    }

    /**
     * Used by the BASIC Auth mechanism for establishing a LoginContext 
     * to authenticate a client/caller/request.
     * 
     * @param username client username
     * @param password client password
     * @return CallbackHandler to be used for establishing a LoginContext
     */
    public static CallbackHandler getUsernamePasswordHandler(
        final String username, final String password) {

        log.debug("username=" + username + "; password=" + password.hashCode());

        final CallbackHandler handler = new CallbackHandler() {
            public void handle(final Callback[] callback) {
                for (int i=0; i<callback.length; i++) {
                    if (callback[i] instanceof NameCallback) {
                        final NameCallback nameCallback = (NameCallback) callback[i];
                        nameCallback.setName(username);
                    } else if (callback[i] instanceof PasswordCallback) {
                        final PasswordCallback passCallback = (PasswordCallback) callback[i];
                        passCallback.setPassword(password.toCharArray());
                    } else {
                        log.warn("Unsupported Callback i=" + i + "; class=" 
                                + callback[i].getClass().getName());
                    }
                }
            }
        };

        return handler;
    }
}
