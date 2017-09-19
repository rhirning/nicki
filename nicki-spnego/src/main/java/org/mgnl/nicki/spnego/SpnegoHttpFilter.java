
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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ietf.jgss.GSSException;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http Servlet Filter that provides <a
 * href="http://en.wikipedia.org/wiki/SPNEGO" target="_blank">SPNEGO</a> authentication.
 * It allows servlet containers like Tomcat and JBoss to transparently/silently
 * authenticate HTTP clients like Microsoft Internet Explorer (MSIE).
 * 
 * <p>
 * This feature in MSIE is sometimes referred to as single sign-on and/or 
 * Integrated Windows Authentication. In general, there are at least two 
 * authentication mechanisms that allow an HTTP server and an HTTP client 
 * to achieve single sign-on: <b>NTLM</b> and <b>Kerberos/SPNEGO</b>.
 * </p>
 * 
 * <p>
 * <b>NTLM</b><br />
 * MSIE has the ability to negotiate NTLM password hashes over an HTTP session 
 * using Base 64 encoded NTLMSSP messages. This is a staple feature of Microsoft's 
 * Internet Information Server (IIS). Open source libraries exists (ie. jCIFS) that 
 * provide NTLM-based authentication capabilities to Servlet Containers. jCIFS uses 
 * NTLM and Microsoft's Active Directory (AD) to authenticate MSIE clients.
 * </p>
 * 
 * <p>
 * <b>{@code SpnegoHttpFilter} does NOT support NTLM (tokens).</b>
 * </p>
 * 
 * <p>
 * <b>Kerberos/SPNEGO</b><br />
 * Kerberos is an authentication protocol that is implemented in AD. The protocol 
 * does not negotiate passwords between a client and a server but rather uses tokens 
 * to securely prove/authenticate to one another over an un-secure network.
 * </p>
 * 
 * <p>
 * <b><code>SpnegoHttpFilter</code> does support Kerberos but through the 
 * pseudo-mechanism <code>SPNEGO</code></b>.
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/SPNEGO" target="_blank">Wikipedia: SPNEGO</a></li>
 * <li><a href="http://www.ietf.org/rfc/rfc4178.txt" target="_blank">IETF RFC: 4178</a></li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Localhost Support</b><br />
 * The Kerberos protocol requires that a service must have a Principal Name (SPN) 
 * specified. However, there are some use-cases where it may not be practical to 
 * specify an SPN (ie. Tomcat running on a developer's machine). The DNS 
 * http://localhost is supported but must be configured in the servlet filter's 
 * init params in the web.xml file. 
 * </p>
 * 
 * <p><b>Modifying the web.xml file</b></p>
 * 
 * <p>Here's an example configuration:</p>
 * 
 * <p>
 * <pre><code>  &lt;filter&gt;
 *      &lt;filter-name&gt;SpnegoHttpFilter&lt;/filter-name&gt;
 *      &lt;filter-class&gt;net.sourceforge.spnego.SpnegoHttpFilter&lt;/filter-class&gt;
 *      
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.allow.basic&lt;/param-name&gt;
 *          &lt;param-value&gt;true&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.allow.localhost&lt;/param-name&gt;
 *          &lt;param-value&gt;true&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.allow.unsecure.basic&lt;/param-name&gt;
 *          &lt;param-value&gt;true&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.login.client.module&lt;/param-name&gt;
 *          &lt;param-value&gt;spnego-client&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *      
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.krb5.conf&lt;/param-name&gt;
 *          &lt;param-value&gt;krb5.conf&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.login.conf&lt;/param-name&gt;
 *          &lt;param-value&gt;login.conf&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.preauth.username&lt;/param-name&gt;
 *          &lt;param-value&gt;Zeus&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.preauth.password&lt;/param-name&gt;
 *          &lt;param-value&gt;Zeus_Password&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.login.server.module&lt;/param-name&gt;
 *          &lt;param-value&gt;spnego-server&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.prompt.ntlm&lt;/param-name&gt;
 *          &lt;param-value&gt;true&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.logger.level&lt;/param-name&gt;
 *          &lt;param-value&gt;1&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *  &lt;/filter&gt;
 *</code></pre>
 * </p>
 * 
 * <p><b>Example usage on web page</b></p>
 * 
 * <p><pre>  &lt;html&gt;
 *  &lt;head&gt;
 *      &lt;title&gt;Hello SPNEGO Example&lt;/title&gt;
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *  Hello &lt;%= request.getRemoteUser() %&gt; !
 *  &lt;/body&gt;
 *  &lt;/html&gt;
 *  </pre>
 * </p>
 *
 * <p>
 * Take a look at the <a href="http://spnego.sourceforge.net/reference_docs.html" 
 * target="_blank">reference docs</a> for other configuration parameters.
 * </p>
 * 
 * <p>See more usage examples at 
 * <a href="http://spnego.sourceforge.net" target="_blank">http://spnego.sourceforge.net</a>
 * </p>
 * 
 * @author Darwin V. Felix
 * 
 */
public final class SpnegoHttpFilter implements Filter {
	private static final Logger LOG = LoggerFactory.getLogger(SpnegoHttpFilter.class);

    private static final String SESSION_NO_USER = "NICKI_SESSION_NO_USER";
    private static final String SESSION_USER = "NICKI_SESSION_USER";

    /** Object for performing Basic and SPNEGO authentication. */
    private transient SpnegoAuthenticator authenticator;
    
    /** Object for performing User Authorization. */
    private transient UserAccessControl accessControl;
    
    /** AuthZ required for every page. */
    private transient String sitewide;
    
    /** Landing page if user is denied authZ access. */
    private transient String page403;
    
    /** directories which should not be authenticated irrespective of filter-mapping. */
    private final transient List<String> excludeDirs = new ArrayList<String>();
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

        try {
            // set some System properties
            final SpnegoFilterConfig config = SpnegoFilterConfig.getInstance();
            this.excludeDirs.addAll(config.getExcludeDirs());
            
            LOG.info("excludeDirs=" + this.excludeDirs);
            
            // pre-authenticate
            this.authenticator = new SpnegoAuthenticator(config);
            
            // authorization
            final Properties props = SpnegoHttpFilter.toProperties(filterConfig);
            if (!props.getProperty("spnego.authz.class", "").isEmpty()) {
                props.put("spnego.server.realm", this.authenticator.getServerRealm());
                this.page403 = props.getProperty("spnego.authz.403", "").trim();
                this.sitewide = props.getProperty("spnego.authz.sitewide", "").trim();
                this.sitewide = (this.sitewide.isEmpty()) ? null : this.sitewide;
                this.accessControl = (UserAccessControl) Class.forName(
                        props.getProperty("spnego.authz.class")).newInstance();
                this.accessControl.init(props);                
            }
            
        } catch (final LoginException lex) {
            throw new ServletException(lex);
        } catch (final GSSException gsse) {
            throw new ServletException(gsse);
        } catch (final PrivilegedActionException pae) {
            throw new ServletException(pae);
        } catch (final FileNotFoundException fnfe) {
            throw new ServletException(fnfe);
        } catch (final URISyntaxException uri) {
            throw new ServletException(uri);
        } catch (InstantiationException iex) {
            throw new ServletException(iex);
        } catch (IllegalAccessException iae) {
            throw new ServletException(iae);
        } catch (ClassNotFoundException cnfe) {
            throw new ServletException(cnfe);
        }
    }

    @Override
    public void destroy() {
        this.page403 = null;
        this.sitewide = null;
        if (null != this.excludeDirs) {
            this.excludeDirs.clear();
        }
        if (null != this.accessControl) {
            this.accessControl.destroy();
            this.accessControl = null;
        }
        if (null != this.authenticator) {
            this.authenticator.dispose();
            this.authenticator = null;
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response
        , final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        LOG.debug("Request: " + httpRequest.getServletPath() + ", ThreadId=" + Thread.currentThread().getId());
        //String browserType = (String) httpRequest.getHeader("User-Agent");
        //LOG.debug("User-Agent: " + browserType);
        
        final SpnegoHttpServletResponse spnegoResponse = new SpnegoHttpServletResponse(
                (HttpServletResponse) response);

        // skip authentication if session is already authenticated
        if (httpRequest.getSession(false) != null) {
        	if (httpRequest.getSession().getAttribute(SESSION_USER) != null) {
        		AppContext.setUser((DynamicObject) httpRequest.getSession(false).getAttribute(SESSION_USER));
                LOG.debug("Authenticated user: " + AppContext.getUser().getDisplayName());
	            chain.doFilter(request, response);
	            return;
        	} else if (httpRequest.getSession(false).getAttribute(SESSION_NO_USER) != null) {
                LOG.debug("no authenticated user");
	            chain.doFilter(request, response);
	            return;
        	}
        }
        // skip authentication if resource is in the list of directories to exclude
        if (exclude(httpRequest.getContextPath(), httpRequest.getServletPath())) {
            LOG.debug("excluded: " + httpRequest.getServletPath());
            chain.doFilter(request, response);
            return;
        }
        
        // client/caller principal
        final SpnegoPrincipal principal;
        try {
            if (httpRequest.getSession(false) != null && httpRequest.getSession(false).getAttribute(SESSION_USER) != null) {
            	httpRequest.getSession().removeAttribute(SESSION_USER);
                httpRequest.getSession().setAttribute(SESSION_NO_USER, "1");
            }
            principal = this.authenticator.authenticate(httpRequest, spnegoResponse);
        } catch (GSSException gsse) {
            LOG.error("HTTP Authorization Header="
                + httpRequest.getHeader(Constants.AUTHZ_HEADER));
            throw new ServletException(gsse);
        } catch (Exception e) {
            httpRequest.getSession().setAttribute(SESSION_NO_USER, "1");
            LOG.debug("Authentication not successful: " + e.getMessage());
            chain.doFilter(request, response);
            return;
        }

        // context/auth loop not yet complete
        if (spnegoResponse.isStatusSet()) {
        	if (httpRequest.getSession(false) != null) {
        		LOG.debug("Second authenticate request");
                chain.doFilter(request, response);
        	} else {
        		LOG.debug("First authenticate request");
        	}
            return;
        }

        // assert
        if (null == principal) {
            LOG.debug("Principal was null.");
            chain.doFilter(request, response);
            return;
        }

        LOG.debug("principal=" + principal);
        
        final SpnegoHttpServletRequest spnegoRequest = 
                new SpnegoHttpServletRequest(httpRequest, principal, this.accessControl);
                
        // site wide authZ check (if enabled)
        if (!isAuthorized((HttpServletRequest) spnegoRequest)) {
            LOG.info("Principal Not AuthoriZed: " + principal);
            if (this.page403.isEmpty()) {
                spnegoResponse.setStatus(HttpServletResponse.SC_FORBIDDEN, true);  
            } else {
                request.getRequestDispatcher(this.page403).forward(spnegoRequest, response);
            }
            return;            
        }
        
        try {
			AppContext.setUser(login(principal.getName()));
			AppContext.setCredentials(principal.getCredential());
			httpRequest.getSession().setAttribute(SESSION_USER, AppContext.getUser());
            httpRequest.getSession().removeAttribute(SESSION_NO_USER);
		} catch (LoginException e) {
			AppContext.setUser(null);
			AppContext.setCredentials(null);
			LOG.debug("Invalid user " + principal.getName());
		}

        chain.doFilter(spnegoRequest, response);
    }
    
	public DynamicObject login(String name) throws LoginException {
		if (StringUtils.isBlank(name)) {
			throw new LoginException("missing userId");
		}
		if (StringUtils.contains(name, "@")) {
			name = StringUtils.substringBefore(name, "@");
		}
		DynamicObject user = loadUser(name);
		if (user == null) {
			LOG.debug("Invalid user " + name);
			throw new LoginException("Invalid user " + name);
		}
		
		LOG.debug("user ok " + user.getPath());
		return user;
	}

	protected DynamicObject loadUser(String userId) {
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
    
    private boolean isAuthorized(final HttpServletRequest request) {
        if (null != this.sitewide && null != this.accessControl
                && !this.accessControl.hasAccess(request.getRemoteUser(), this.sitewide)) {
            return false;
        }

        return true;
    }
    
    private boolean exclude(final String contextPath, final String servletPath) {
        // each item in excludeDirs ends with a slash
        final String path = contextPath + servletPath + (servletPath.endsWith("/") ? "" : "/");
        
        for (String dir : this.excludeDirs) {
            if (path.startsWith(dir)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static Properties toProperties(final FilterConfig filterConfig) {
        final Properties props = new Properties();
        @SuppressWarnings("unchecked")
        final Enumeration<String> it = filterConfig.getInitParameterNames();
        
        while (it.hasMoreElements()) {
            final String key = it.nextElement();
            props.put(key, filterConfig.getInitParameter(key));
        }
        
        return props;
    }
    
    /**
     * Defines constants and parameter names that are used in the  
     * web.xml file, and HTTP request headers, etc.
     * 
     * <p>
     * This class is primarily used internally or by implementers of 
     * custom http clients and by {@link SpnegoFilterConfig}.
     * </p>
     * 
     */
    public static final class Constants {

        private Constants() {
            // default private
        }
        
        /** 
         * Servlet init param name in web.xml <b>spnego.allow.basic</b>.
         * 
         * <p>Set this value to <code>true</code> in web.xml if the filter 
         * should allow Basic Authentication.</p>
         * 
         * <p>It is recommended that you only allow Basic Authentication 
         * if you have clients that cannot perform Kerberos authentication. 
         * Also, you should consider requiring SSL/TLS by setting 
         * <code>spnego.allow.unsecure.basic</code> to <code>false</code>.</p>
         */
        public static final String ALLOW_BASIC = "spnego.allow.basic";

        /**
         * Servlet init param name in web.xml <b>spnego.allow.delegation</b>.
         * 
         * <p>Set this value to <code>true</code> if server should support 
         * credential delegation requests.</p>
         * 
         * <p>Take a look at the {@link DelegateServletRequest} for more 
         * information about other pre-requisites.</p>
         */
        public static final String ALLOW_DELEGATION = "spnego.allow.delegation";
        
        /**
         * Servlet init param name in web.xml <b>spnego.allow.localhost</b>.
         * 
         * <p>Flag to indicate if requests coming from http://localhost 
         * or http://127.0.0.1 should not be authenticated using 
         * Kerberos.</p>
         * 
         * <p>This feature helps to obviate the requirement of 
         * creating an SPN for developer machines.</p>
         * 
         */
        public static final String ALLOW_LOCALHOST = "spnego.allow.localhost";
        
        /** 
         * Servlet init param name in web.xml <b>spnego.allow.unsecure.basic</b>.
         * 
         * <p>Set this value to <code>false</code> in web.xml if the filter 
         * should reject connections that do not use SSL/TLS.</p>
         */
        public static final String ALLOW_UNSEC_BASIC = "spnego.allow.unsecure.basic";
        
        /** 
         * HTTP Response Header <b>WWW-Authenticate</b>. 
         * 
         * <p>The filter will respond with this header with a value of "Basic" 
         * and/or "Negotiate" (based on web.xml file).</p>
         */
        public static final String AUTHN_HEADER = "WWW-Authenticate";
        
        /** 
         * HTTP Request Header <b>Authorization</b>. 
         * 
         * <p>Clients should send this header where the value is the 
         * authentication token(s).</p>
         */
        public static final String AUTHZ_HEADER = "Authorization";
        
        /** 
         * HTTP Response Header <b>Basic</b>. 
         * 
         * <p>The filter will set this as the value for the "WWW-Authenticate" 
         * header if "Basic" auth is allowed (based on web.xml file).</p>
         */
        public static final String BASIC_HEADER = "Basic";
        
        /** 
         * Servlet init param name in web.xml <b>spnego.login.client.module</b>. 
         * 
         * <p>The LoginModule name that exists in the login.conf file.</p>
         */
        public static final String CLIENT_MODULE = "spnego.login.client.module";

        /** 
         * HTTP Request Header <b>Content-Type</b>. 
         * 
         */
        public static final String CONTENT_TYPE = "Content-Type";
        
        /** 
         * Servlet init param name in web.xml <b>spnego.exclude.dirs</b>.
         * 
         * <p>
         * A List of URL paths, starting at the context root, 
         * that should NOT undergo authentication (authN). 
         * </p>
         */
        public static final String EXCLUDE_DIRS = "spnego.exclude.dirs";
        
        /** 
         * Servlet init param name in web.xml <b>spnego.krb5.conf</b>. 
         * 
         * <p>The location of the krb5.conf file. On Windows, this file will 
         * sometimes be named krb5.ini and reside <code>%WINDOWS_ROOT%/krb5.ini</code> 
         * here.</p>
         * 
         * <p>By default, Java looks for the file in these locations and order:
         * <li>System Property (java.security.krb5.conf)</li>
         * <li>%JAVA_HOME%/lib/security/krb5.conf</li>
         * <li>%WINDOWS_ROOT%/krb5.ini</li>
         * </p>
         */
        public static final String KRB5_CONF = "spnego.krb5.conf";
        
        /**
         * Specify logging level.

         * <pre>
         * 1 = FINEST
         * 2 = FINER
         * 3 = FINE
         * 4 = CONFIG
         * 5 = INFO
         * 6 = WARNING
         * 7 = SEVERE
         * </pre>
         * 
         */
        static final String LOGGER_LEVEL = "spnego.logger.level";
        
        /**
         * Name of Spnego Logger.
         * 
         * <p>Example: <code>Logger.getLogger(Constants.LOGGER_NAME)</code></p>
         */
        static final String LOGGER_NAME = "SpnegoHttpFilter"; 
        
        /** 
         * Servlet init param name in web.xml <b>spnego.login.conf</b>. 
         * 
         * <p>The location of the login.conf file.</p>
         */
        public static final String LOGIN_CONF = "spnego.login.conf";
        
        /** 
         * HTTP Response Header <b>Negotiate</b>. 
         * 
         * <p>The filter will set this as the value for the "WWW-Authenticate" 
         * header. Note that the filter may also add another header with 
         * a value of "Basic" (if allowed by the web.xml file).</p>
         */
        public static final String NEGOTIATE_HEADER = "Negotiate";
        
        /**
         * NTLM base64-encoded token start value.
         */
        static final String NTLM_PROLOG = "TlRMTVNT";
        
        /** 
         * Servlet init param name in web.xml <b>spnego.preauth.password</b>. 
         * 
         * <p>Network Domain password. For Windows, this is sometimes known 
         * as the Windows NT password.</p>
         */
        public static final String PREAUTH_PASSWORD = "spnego.preauth.password";
        
        /** 
         * Servlet init param name in web.xml <b>spnego.preauth.username</b>. 
         * 
         * <p>Network Domain username. For Windows, this is sometimes known 
         * as the Windows NT username.</p>
         */
        public static final String PREAUTH_USERNAME = "spnego.preauth.username";
        
        /**
         * If server receives an NTLM token, the filter will return with a 401 
         * and with Basic as the only option (no Negotiate) <b>spnego.prompt.ntlm</b>. 
         */
        public static final String PROMPT_NTLM = "spnego.prompt.ntlm";
        
        /** 
         * Servlet init param name in web.xml <b>spnego.login.server.module</b>. 
         * 
         * <p>The LoginModule name that exists in the login.conf file.</p>
         */
        public static final String SERVER_MODULE = "spnego.login.server.module";
        
        /** 
         * HTTP Request Header <b>SOAPAction</b>. 
         * 
         */
        public static final String SOAP_ACTION = "SOAPAction";
    }
}
