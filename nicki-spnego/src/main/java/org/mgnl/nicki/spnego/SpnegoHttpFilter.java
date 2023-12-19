
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.util.Classes;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class SpnegoHttpFilter implements Filter {

    private static final String SESSION_NO_USER = "NICKI_SESSION_NO_USER";
    private static final String SESSION_USER = "NICKI_SESSION_USER";
    public static final String SESSION_AUTH_HEADER = "NICKI_SESSION_AUTH_HEADER";
	public static final String SESSION_AUTH_REQUEST = "NICKI_SESSION_AUTH_REQUEST";
	
	public static final String NICKI_PARAMETERS = "NICKI_PARAMETERS";
	private static final String[] PARAMETERS = {
		"nickiToken", "nickiName", "nickiPassword"
	};

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
    
    private boolean active;
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    	this.active = Config.getBoolean("spnego.kerberos.active", false);

    	if (active) {
	        try {
	            // set some System properties
	            final SpnegoFilterConfig config = SpnegoFilterConfig.getInstance();
	            this.excludeDirs.addAll(config.getExcludeDirs());
	            
	            log.info("excludeDirs=" + this.excludeDirs);
	            
	            // pre-authenticate
	            this.authenticator = new SpnegoAuthenticator(config);
	            
	            // authorization
	            final Properties props = SpnegoHttpFilter.toProperties(filterConfig);
	            if (!props.getProperty("spnego.authz.class", "").isEmpty()) {
	            	log.debug("spnego.authz.class = " + props.getProperty("spnego.authz.class", ""));
	                props.put("spnego.server.realm", this.authenticator.getServerRealm());
	                this.page403 = props.getProperty("spnego.authz.403", "").trim();
	                this.sitewide = props.getProperty("spnego.authz.sitewide", "").trim();
	                this.sitewide = (this.sitewide.isEmpty()) ? null : this.sitewide;
	                this.accessControl = (UserAccessControl) Classes.newInstance(
	                        props.getProperty("spnego.authz.class"));
	                this.accessControl.init(props);                
	            } else {
	            	log.debug("spnego.authz.class empty");
	            	
	            }
	            
	        } catch (final Exception e) {
	        	log.error("Error inizializing SpnegoHttpFilter", e);
	    		log.info("Kerberos not activated");
	        	this.active = false;
	        }
    	} else {
    		log.info("Kerberos not activated");
    	}
    }

    @Override
    public void destroy() {
    	if (active) {
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
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response
        , final FilterChain chain) throws IOException, ServletException {
    	if (!active) {
    		chain.doFilter(request, response);
    		return;
    	}
    	
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
       	AppContext.setRequest(httpRequest);
        
		if (httpRequest.getSession(true).getAttribute(NICKI_PARAMETERS) == null) {
			Map<String, String> map = new HashMap<String, String>();
			for (String param : PARAMETERS) {
				if (StringUtils.isNotBlank(httpRequest.getParameter(param))) {
					map.put(param, httpRequest.getParameter(param));
				}
			}
			httpRequest.getSession().setAttribute(NICKI_PARAMETERS, map);
			log.debug("ParameterMap: " + map);
		}

    	// check for request parameter nokerberos
        if (httpRequest.getParameter("nokerberos") != null) {
        	httpRequest.getSession().setAttribute(SESSION_NO_USER, "1");
        }
        
        log.debug("Request: " + httpRequest.getServletPath() + ", ThreadId=" + Thread.currentThread().getId());
        //String browserType = (String) httpRequest.getHeader("User-Agent");
        //log.debug("User-Agent: " + browserType);
        
    	String authHeader = httpRequest.getHeader(Constants.AUTHZ_HEADER);

    	if (StringUtils.isNotBlank(authHeader)) {
        	httpRequest.getSession().setAttribute(SpnegoHttpFilter.SESSION_AUTH_HEADER, authHeader);
    	}
    	
        // skip authentication if session is already authenticated
    	if (httpRequest.getSession().getAttribute(SESSION_USER) != null) {
            log.debug("Authenticated user: " + httpRequest.getSession().getAttribute(SESSION_USER));
            chain.doFilter(request, response);
            return;
    	} else if (httpRequest.getSession(false).getAttribute(SESSION_NO_USER) != null) {
            log.debug("no authenticated user");
            chain.doFilter(request, response);
            return;
    	}
        // skip authentication if resource is in the list of directories to exclude
        if (exclude(httpRequest.getContextPath(), httpRequest.getServletPath())) {
            log.debug("excluded: " + httpRequest.getServletPath());
            chain.doFilter(request, response);
            return;
        }
        
        final SpnegoHttpServletResponse spnegoResponse = new SpnegoHttpServletResponse(
                (HttpServletResponse) response);

        // client/caller principal
        final SpnegoPrincipal principal;
        try {
            principal = this.authenticator.authenticate(httpRequest, spnegoResponse);
        } catch (GSSException gsse) {
            log.error("HTTP Authorization Header="
                + httpRequest.getHeader(Constants.AUTHZ_HEADER));
        	httpRequest.getSession().setAttribute(SESSION_NO_USER, "1");
            log.info("Invalid Authorization Header");
            chain.doFilter(request, response);
            return;
        } catch (NotSupportedException e) {
        	httpRequest.getSession().setAttribute(SESSION_NO_USER, "1");
            log.info("Authentication not supported");
            chain.doFilter(request, response);
            return;
        } catch (IOException e) {
            httpRequest.getSession().setAttribute(SESSION_NO_USER, "1");
            log.info("Authentication not successful: " + e.getMessage());
            chain.doFilter(request, response);
            return;
		}

        // context/auth loop not yet complete
        if (spnegoResponse.isStatusSet()) {
        	if (httpRequest.getSession().getAttribute(SESSION_AUTH_REQUEST) != null) {
        		log.debug("Second authenticate request");
            	httpRequest.getSession().setAttribute(SESSION_NO_USER, "1");
                chain.doFilter(request, response);
        	} else {
        		log.debug("First authenticate request");
        		httpRequest.getSession().setAttribute(SESSION_AUTH_REQUEST, "1");
        	}
            return;
        }

        // assert
        if (null == principal) {
            log.debug("Principal was null.");
        	httpRequest.getSession().setAttribute(SESSION_NO_USER, "1");
            chain.doFilter(request, response);
            return;
        }

        log.debug("principal=" + principal);
        
        final SpnegoHttpServletRequest spnegoRequest = 
                new SpnegoHttpServletRequest(httpRequest, principal, this.accessControl);
                
        // site wide authZ check (if enabled)
        if (!isAuthorized((HttpServletRequest) spnegoRequest)) {
            log.info("Principal Not AuthoriZed: " + principal);
            if (this.page403.isEmpty()) {
                spnegoResponse.setStatus(HttpServletResponse.SC_FORBIDDEN, true);  
            } else {
                request.getRequestDispatcher(this.page403).forward(spnegoRequest, response);
            }
            return;            
        }
        
		httpRequest.getSession().setAttribute(SESSION_USER, principal.getName());
        httpRequest.getSession().removeAttribute(SESSION_NO_USER);

        chain.doFilter(spnegoRequest, response);
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
