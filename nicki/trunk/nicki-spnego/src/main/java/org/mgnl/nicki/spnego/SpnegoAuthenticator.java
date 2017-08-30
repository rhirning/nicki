/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.spnego;

import java.io.IOException;
import java.security.PrivilegedActionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mgnl.nicki.spnego.SpnegoHttpFilter.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;

/**
 * Handles <a href="http://en.wikipedia.org/wiki/SPNEGO">SPNEGO</a> or <a
 * href="http://en.wikipedia.org/wiki/Basic_access_authentication">Basic</a>
 * authentication.
 * 
 * <p>
 * Be cautious about who you give a reference to.</b>
 * </p>
 * 
 * <p>
 * Basic Authentication must be enabled through the filter configuration. See
 * an example web.xml configuration in the <a href="http://spnego.sourceforge.net/spnego_tomcat.html" 
 * target="_blank">installing on tomcat</a> documentation or the 
 * {@link SpnegoHttpFilter} javadoc. 
 * </p>
 * 
 * <p>
 * Localhost is supported but must be enabled through the filter configuration. Allowing 
 * requests to come from the DNS http://localhost will obviate the requirement that a 
 * service must have an SPN. <b>Note that Kerberos authentication (if localhost) does 
 * not occur but instead simply returns the <code>System.getProperty("user.name")</code> 
 * or the Server's pre-authentication username.</b>
 * </p>
 * 
 * <p>
 * NTLM tokens are NOT supported. However it is still possible to avoid an error 
 * being returned by downgrading the authentication from Negotiate NTLM to Basic Auth.
 * </p>
 * 
 * <p>
 * See the <a href="http://spnego.sourceforge.net/reference_docs.html" 
 * target="_blank">reference docs</a> on how to configure the web.xml to prompt 
 * when if a request is being made using NTLM.
 * </p>
 * 
 * <p>
 * Finally, to see a working example and instructions on how to use a keytab, take 
 * a look at the <a href="http://spnego.sourceforge.net/server_keytab.html"
 * target="_blank">creating a server keytab</a> example.
 * </p>
 * 
 * @author Darwin V. Felix
 * 
 */
public final class SpnegoAuthenticator {

    private static final Logger LOG = LoggerFactory.getLogger(SpnegoAuthenticator.class);
    
    /** GSSContext is not thread-safe. */
    private static final Lock LOCK = new ReentrantLock();
    
    /** Default GSSManager. */
    private static final GSSManager MANAGER = GSSManager.getInstance();
    
    /** Flag to indicate if BASIC Auth is allowed. */
    private final transient boolean allowBasic;
    
    /** Flag to indicate if credential delegation is allowed. */
    private final transient boolean allowDelegation;

    /** Flag to skip auth if localhost. */
    private final transient boolean allowLocalhost;

    /** Flag to indicate if non-SSL BASIC Auth allowed. */
    private final transient boolean allowUnsecure;
    
    /** Flag to indicate if NTLM is accepted. */
    private final transient boolean promptIfNtlm;

    /** Login Context module name for client auth. */
    private final transient String clientModuleName;

    /** Login Context server uses for pre-authentication. */
    private final transient LoginContext loginContext;

    /** Credentials server uses for authenticating requests. */
    private final transient GSSCredential serverCredentials;
    
    /** Server Principal used for pre-authentication. */
    private final transient KerberosPrincipal serverPrincipal;

    /**
     * Create an authenticator for SPNEGO and/or BASIC authentication.
     * 
     * @param config servlet filter initialization parameters
     * @throws LoginException 
     * @throws GSSException 
     * @throws PrivilegedActionException 
     */
    public SpnegoAuthenticator(final SpnegoFilterConfig config) 
        throws LoginException, GSSException, PrivilegedActionException {

        LOG.debug("config=" + config);

        this.allowBasic = config.isBasicAllowed();
        this.allowUnsecure = config.isUnsecureAllowed();  
        this.clientModuleName = config.getClientLoginModule();
        this.allowLocalhost = config.isLocalhostAllowed();
        this.promptIfNtlm = config.downgradeNtlm();
        this.allowDelegation = config.isDelegationAllowed();

        if (config.useKeyTab()) {
            this.loginContext = new LoginContext(config.getServerLoginModule());
        } else {
            final CallbackHandler handler = SpnegoProvider.getUsernamePasswordHandler(
                    config.getPreauthUsername()
                    , config.getPreauthPassword());

            this.loginContext = new LoginContext(config.getServerLoginModule(), handler);            
        }

        this.loginContext.login();

        this.serverCredentials = SpnegoProvider.getServerCredential(
                this.loginContext.getSubject());

        this.serverPrincipal = new KerberosPrincipal(
                this.serverCredentials.getName().toString());
    }
    
    /**
     * Create an authenticator for SPNEGO and/or BASIC authentication.
     * 
     * @param loginModuleName module named defined in login.conf
     * @param config servlet filter initialization parameters
     * @throws LoginException 
     * @throws GSSException 
     * @throws PrivilegedActionException 
     */
    public SpnegoAuthenticator(final String loginModuleName
        , final SpnegoFilterConfig config) throws LoginException
        , GSSException, PrivilegedActionException {

        LOG.debug("loginModuleName=" + loginModuleName);

        this.allowBasic = config.isBasicAllowed();
        this.allowUnsecure = config.isUnsecureAllowed();  
        this.clientModuleName = config.getClientLoginModule();
        this.allowLocalhost = config.isLocalhostAllowed();
        this.promptIfNtlm = config.downgradeNtlm();
        this.allowDelegation = config.isDelegationAllowed();

        final String username = config.getPreauthUsername();
        final boolean hasUsername = null != username && !username.trim().isEmpty();
        
        if (hasUsername) {
            this.loginContext = new LoginContext(loginModuleName
                , SpnegoProvider.getUsernamePasswordHandler(username
                    , config.getPreauthPassword()));
        } else if (config.useKeyTab()) {
            this.loginContext = new LoginContext(loginModuleName);
        } else {
            throw new IllegalArgumentException(
                "Must provide a username/password or specify a keytab file");
        }

        this.loginContext.login();

        this.serverCredentials = SpnegoProvider.getServerCredential(
                this.loginContext.getSubject());

        this.serverPrincipal = new KerberosPrincipal(
                this.serverCredentials.getName().toString());
    }
    
    /**
     * Returns the KerberosPrincipal of the user/client making the HTTP request.
     * 
     * <p>
     * Null may be returned if client did not provide auth info.
     * </p>
     * 
     * <p>
     * Method will throw UnsupportedOperationException if client authz 
     * request is NOT "Negotiate" or "Basic". 
     * </p>
     * @param req servlet request
     * @param resp servlet response
     * 
     * @return null if auth not complete else SpnegoPrincipal of client
     * @throws GSSException 
     * @throws IOException 
     */
    public SpnegoPrincipal authenticate(final HttpServletRequest req
        , final SpnegoHttpServletResponse resp) throws GSSException
        , IOException {
                
        // Skip auth if localhost
        if (this.allowLocalhost && this.isLocalhost(req)) {
            return doLocalhost();
        }

        // domain/realm of server
        final String serverRealm = this.serverPrincipal.getRealm();
        
        // determine if we allow basic
        final boolean basicSupported = 
            this.allowBasic && (this.allowUnsecure || req.isSecure());
        
        final SpnegoPrincipal principal;
        final SpnegoAuthScheme scheme = SpnegoProvider.negotiate(
                req, resp, basicSupported, this.promptIfNtlm, serverRealm);
        
        // NOTE: this may also occur if we do not allow Basic Auth and
        // the client only supports Basic Auth
        if (null == scheme) {
            LOG.debug("scheme null.");
            return null;
        } else {
        	LOG.debug("scheme: " + scheme.getScheme());
        }

        // NEGOTIATE scheme
        if (scheme.isNegotiateScheme()) {
            principal = doSpnegoAuth(scheme, resp);
        	LOG.debug("principal(SPNEGO): " + (principal == null?null:principal.getName()));
            
        // BASIC scheme
        } else if (scheme.isBasicScheme()) {
            // check if we allow Basic Auth AND if can be un-secure
            if (basicSupported) {
                principal = doBasicAuth(scheme, resp);
            	LOG.debug("principal(BASIC): " + (principal == null?null:principal.getName()));
            } else {
                LOG.error("allowBasic=" + this.allowBasic 
                        + "; allowUnsecure=" + this.allowUnsecure
                        + "; req.isSecure()=" + req.isSecure());
                throw new UnsupportedOperationException("Basic Auth not allowed"
                        + " or SSL required.");
            }

        // Unsupported scheme
        } else {
            throw new UnsupportedOperationException("scheme=" + scheme);
        }

        return principal;
    }
    
    /**
     * Logout. Since server uses LoginContext to login/pre-authenticate, we must
     * also logout when we are done using this object.
     * 
     * <p>
     * Generally, instantiators of this class should be the only to call 
     * dispose() as it indicates that this class will no longer be used.
     * </p>
     */
    public void dispose() {
        if (null != this.serverCredentials) {
            try {
                this.serverCredentials.dispose();
            } catch (GSSException e) {
                LOG.warn("Dispose failed.", e);
            }
        }
        if (null != this.loginContext) {
            try {
                this.loginContext.logout();
            } catch (LoginException lex) {
                LOG.warn("Logout failed.", lex);
            }
        }
    }
    
    /**
     * Performs authentication using the BASIC Auth mechanism.
     *
     * <p>
     * Returns null if authentication failed or if the provided 
     * the auth scheme did not contain BASIC Auth data/token.
     * </p>
     * 
     * @return SpnegoPrincipal for the given auth scheme.
     */
    private SpnegoPrincipal doBasicAuth(final SpnegoAuthScheme scheme
        , final SpnegoHttpServletResponse resp) throws IOException {

        final byte[] data = scheme.getToken();

        if (0 == data.length) {
            LOG.debug("Basic Auth data was NULL.");
            return null;
        }

        final String[] basicData = new String(data).split(":", 2);

        // assert
        if (basicData.length != 2) {
            throw new IllegalArgumentException("Username/Password may"
                    + " have contained an invalid character. basicData.length=" 
                    + basicData.length);
        }

        // substring to remove domain (if provided)
        final String username = basicData[0].substring(basicData[0].indexOf('\\') + 1);
        final String password = basicData[1];
        final CallbackHandler handler = SpnegoProvider.getUsernamePasswordHandler(
                username, password);
        
        SpnegoPrincipal principal = null;
        
        try {
            // assert
            if (null == username || username.isEmpty()) {
                throw new LoginException("Username is required.");
            }

            final LoginContext cntxt = new LoginContext(this.clientModuleName, handler);

            // validate username/password by login/logout
            LOG.debug("vor login");
            cntxt.login();
            LOG.debug("nach login / vor logout");
            cntxt.logout();
            LOG.debug("nach logout");

            principal = new SpnegoPrincipal(username + '@' 
                    + this.serverPrincipal.getRealm()
                    , KerberosPrincipal.KRB_NT_PRINCIPAL,
                    password.getBytes());

        } catch (LoginException lex) {
            LOG.debug(lex.getMessage() + ": Login failed. username=" + username);

            resp.setHeader(Constants.AUTHN_HEADER, Constants.NEGOTIATE_HEADER);
            resp.addHeader(Constants.AUTHN_HEADER, Constants.BASIC_HEADER 
                    + " realm=\"" + this.serverPrincipal.getRealm() + '\"');

            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED, true);
        }

        return principal;
    }

    private SpnegoPrincipal doLocalhost() {
        final String username = System.getProperty("user.name");
        
        if (null == username || username.isEmpty()) {
            return new SpnegoPrincipal(this.serverPrincipal.getName() + '@' 
                    + this.serverPrincipal.getRealm()
                    , this.serverPrincipal.getNameType(), null);            
        } else {
            return new SpnegoPrincipal(username + '@' 
                    + this.serverPrincipal.getRealm()
                    , KerberosPrincipal.KRB_NT_PRINCIPAL, null);            
        }
    }

    /**
     * Performs authentication using the SPNEGO mechanism.
     *
     * <p>
     * Returns null if authentication failed or if the provided 
     * the auth scheme did not contain the SPNEGO/GSS token.
     * </p>
     * 
     * @return SpnegoPrincipal for the given auth scheme.
     */
    private SpnegoPrincipal doSpnegoAuth(
        final SpnegoAuthScheme scheme, final SpnegoHttpServletResponse resp) 
        throws GSSException, IOException {

        final String principal;
        final byte[] gss = scheme.getToken();

        if (0 == gss.length) {
            LOG.debug("GSS data was NULL.");
            return null;
        }

        GSSContext context = null;
        GSSCredential delegCred = null;
        
        try {
            final byte[] token;
            
            SpnegoAuthenticator.LOCK.lock();
            try {
                context = SpnegoAuthenticator.MANAGER.createContext(this.serverCredentials);
                token = context.acceptSecContext(gss, 0, gss.length);
            } finally {
                SpnegoAuthenticator.LOCK.unlock();
            }

            if (null == token) {
                LOG.debug("Token was NULL.");
                return null;
            }

            resp.setHeader(Constants.AUTHN_HEADER, Constants.NEGOTIATE_HEADER 
                    + ' ' + Base64.encodeBase64(token));

            if (!context.isEstablished()) {
                LOG.debug("context not established");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED, true);
                return null;
            }

            principal = context.getSrcName().toString();
            
            if (this.allowDelegation && context.getCredDelegState()) {
                delegCred = context.getDelegCred();
            }

        } finally {
            if (null != context) {
                SpnegoAuthenticator.LOCK.lock();
                try {
                    context.dispose();
                } finally {
                    SpnegoAuthenticator.LOCK.unlock();
                }
            }
        }

        return new SpnegoPrincipal(principal, KerberosPrincipal.KRB_NT_PRINCIPAL, gss, delegCred);
    }
    
    public String getServerRealm() {
        return this.serverPrincipal.getRealm();
    }

    /**
     * Returns true if HTTP request is from the same host (localhost).
     * 
     * @param req servlet request
     * @return true if HTTP request is from the same host (localhost)
     */
    private boolean isLocalhost(final HttpServletRequest req) {
        boolean isLocal = req.getLocalAddr().equals(req.getRemoteAddr());
        
        if (!isLocal && "0.0.0.0".equals(req.getLocalAddr()) // NOPMD
                && "0:0:0:0:0:0:0:1".equals(req.getRemoteAddr())) { // NOPMD
            isLocal = true;
        }
        
        return isLocal;
    }
}
