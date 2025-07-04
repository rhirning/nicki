
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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.mgnl.nicki.spnego.SpnegoHttpFilter.Constants;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;


/**
 * This Class may be used by custom clients as a convenience when connecting 
 * to a protected HTTP server.
 * 
 * <p>
 * This mechanism is an alternative to HTTP Basic Authentication where the 
 * HTTP server does not support Basic Auth but instead has SPNEGO support 
 * (take a look at {@link SpnegoHttpFilter}).
 * </p>
 * 
 * <p>
 * A krb5.conf and a login.conf is required when using this class. Take a 
 * look at the <a href="http://spnego.sourceforge.net" target="_blank">spnego.sourceforge.net</a> 
 * documentation for an example krb5.conf and login.conf file. 
 * Also, you must provide a keytab file, or a username and password, or allowtgtsessionkey.
 * </p>
 * 
 * <p>
 * Example usage (username/password):
 * <pre>
 *     public static void main(final String[] args) throws Exception {
 *         System.setProperty("java.security.krb5.conf", "krb5.conf");
 *         System.setProperty("sun.security.krb5.debug", "true");
 *         System.setProperty("java.security.auth.login.config", "login.conf");
 *         
 *         SpnegoHttpURLConnection spnego = null;
 *         
 *         try {
 *             spnego = new SpnegoHttpURLConnection("spnego-client", "dfelix", "myp@s5");
 *             spnego.connect(new URL("http://medusa:8080/index.jsp"));
 *             
 *             System.out.println(spnego.getResponseCode());
 *         
 *         } finally {
 *             if (null != spnego) {
 *                 spnego.disconnect();
 *             }
 *         }
 *     }
 * </pre>
 * </p>
 * 
 * <p>
 * Alternatively, if the server supports HTTP Basic Authentication, this Class 
 * is NOT needed and instead you can do something like the following:
 * <pre>
 *     public static void main(final String[] args) throws Exception {
 *         final String creds = "dfelix:myp@s5";
 *         
 *         final String token = Base64.encode(creds.getBytes());
 *         
 *         URL url = new URL("http://medusa:8080/index.jsp");
 *         
 *         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
 *         
 *         conn.setRequestProperty(Constants.AUTHZ_HEADER
 *                 , Constants.BASIC_HEADER + " " + token);
 *                 
 *         conn.connect();
 *         
 *         System.out.println("Response Code:" + conn.getResponseCode());
 *     }
 * </pre>
 * </p>
 * 
 * <p>
 * To see a working example and instructions on how to use a keytab, take 
 * a look at the <a href="http://spnego.sourceforge.net/client_keytab.html"
 * target="_blank">creating a client keytab</a> example.
 * </p>
 * 
 * <p>
 * Finally, the {@link SpnegoSOAPConnection} class is another example of a class 
 * that uses this class.
 * <p>
 * 
 * @author Darwin V. Felix
 * 
 */
@Slf4j
public final class SpnegoHttpURLConnection {

    /** GSSContext is not thread-safe. */
    private static final Lock LOCK = new ReentrantLock();
    
    /** The Constant EMPTY_BYTE. */
    private static final byte[] EMPTY_BYTE = new byte[0];

    /**
     * If false, this connection object has not created a communications link to 
     * the specified URL. If true, the communications link has been established.
     */
    private transient boolean connected = false;

    /**
     * Default is set to false instead of true.
     * 
     * @see java.net.HttpURLConnection#getInstanceFollowRedirects()
     */
    private boolean instanceFollowRedirects = true;

    /**
     * Default is GET.
     * 
     * @see java.net.HttpURLConnection#getRequestMethod()
     */
    private transient String requestMethod = "GET";
    
    /**
     * The request properties.
     *
     * @see java.net.URLConnection#getRequestProperties()
     */
    private final transient Map<String, List<String>> requestProperties = 
        new LinkedHashMap<String, List<String>>();

    /** 
     * Login Context for authenticating client. If username/password 
     * or GSSCredential is provided (in constructor) then this 
     * field will always be null.
     */
    private final transient LoginContext loginContext;

    /**
     * Client's credentials. If username/password or LoginContext is provided 
     * (in constructor) then this field will always be null.
     */
    private transient GSSCredential credential;

    /** 
     * Flag to determine if GSSContext has been established. Users of this 
     * class should always check that this field is true before using/trusting 
     * the contents of the response.
     */
    private transient boolean cntxtEstablished = false;

    /** 
     * Ref to HTTP URL Connection object after calling connect method. 
     * Always call spnego.disconnect() when done using this class.
     */
    private transient HttpURLConnection conn = null;

    /** 
     * Request credential to be delegated. 
     * Default is false. 
     */
    private transient boolean reqCredDeleg = false;
    
    /**
     * Determines if the GSSCredentials (if any) used during the 
     * connection request should be automatically disposed by 
     * this class when finished.
     * Default is true.
     */
    private transient boolean autoDisposeCreds = true;
    
    /**
     * Number of times request was redirected.
     */
    private transient int redirectCount = 0;

    /**
     * GSSContext request Mutual Authentication.
     */
    private transient boolean mutualAuth = true;
    
    /**
     * GSSContext request Message Confidentiality.
     * Default is true.
     */
    private transient boolean confidentiality = true;
    
    /**
     * GSSContext request Message Integrity.
     * Default is true.
     */
    private transient boolean messageIntegrity = true;
    
    /**
     * GSSContext request Replay Detection.
     * Default is true.
     */
    private transient boolean replayDetection = true;
    
    /**
     * GSSContext request Sequence Detection.
     * Default is true.
     */
    private transient boolean sequenceDetection = true;
    
    /**
     * Number of times redirects will be allowed.
     */
    private static final int MAX_REDIRECTS = 20;

    /**
     * Creates an instance where the LoginContext relies on a keytab 
     * file being specified by "java.security.auth.login.config" or 
     * where LoginContext relies on tgtsessionkey.
     *
     * @param loginModuleName the login module name
     * @throws LoginException the login exception
     */
    public SpnegoHttpURLConnection(final String loginModuleName)
        throws LoginException {

        this.loginContext = new LoginContext(loginModuleName);
        this.loginContext.login();
        this.credential = null;
    }
    
    /**
     * Create an instance where the GSSCredential is specified by the parameter 
     * and where the GSSCredential is automatically disposed after use.
     *  
     * @param creds credentials to use
     */
    public SpnegoHttpURLConnection(final GSSCredential creds) {
        this(creds, true);
    }
    
    /**
     * Create an instance where the GSSCredential is specified by the parameter 
     * and whether the GSSCredential should be disposed after use.
     * 
     * @param creds credentials to use
     * @param dispose true if GSSCredential should be diposed after use
     */
    public SpnegoHttpURLConnection(final GSSCredential creds, final boolean dispose) {
        this.loginContext = null;
        this.credential = creds;
        this.autoDisposeCreds = dispose;
    }

    /**
     * Creates an instance where the LoginContext does not require a keytab
     * file. However, the "java.security.auth.login.config" property must still
     * be set prior to instantiating this object.
     *
     * @param loginModuleName the login module name
     * @param username the username
     * @param password the password
     * @throws LoginException the login exception
     */
    public SpnegoHttpURLConnection(final String loginModuleName,
        final String username, final String password) throws LoginException {

        final CallbackHandler handler = SpnegoProvider.getUsernamePasswordHandler(
                username, password);

        this.loginContext = new LoginContext(loginModuleName, handler);
        this.loginContext.login();
        this.credential = null;
    }

    /**
     * Throws IllegalStateException if this connection object has not yet created 
     * a communications link to the specified URL.
     */
    private void assertConnected() {
        if (!this.connected) {
            throw new IllegalStateException("Not connected.");
        }
    }

    /**
     * Throws IllegalStateException if this connection object has already created 
     * a communications link to the specified URL.
     */
    private void assertNotConnected() {
        if (this.connected) {
            throw new IllegalStateException("Already connected.");
        }
    }

    /**
     * Opens a communications link to the resource referenced by 
     * this URL, if such a connection has not already been established.
     * 
     * <p>
     * This implementation simply calls this objects 
     * connect(URL, ByteArrayOutputStream) method but passing in a null 
     * for the second argument.
     * </p>
     *
     * @param url the url
     * @return an HttpURLConnection object
     * @throws GSSException the GSS exception
     * @throws PrivilegedActionException the privileged action exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @see java.net.URLConnection#connect()
     */
    public HttpURLConnection connect(final URL url)
        throws GSSException, PrivilegedActionException, IOException {
        
        return this.connect(url, null);
    }

    /**
     * Opens a communications link to the resource referenced by 
     * this URL, if such a connection has not already been established.
     *
     * @param url the url
     * @param dooutput optional message/payload to send to server
     * @return an HttpURLConnection object
     * @throws GSSException the GSS exception
     * @throws PrivilegedActionException the privileged action exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @see java.net.URLConnection#connect()
     */
    public HttpURLConnection connect(final URL url, final ByteArrayOutputStream dooutput)
        throws GSSException, PrivilegedActionException, IOException {
        
        // assert
        if (!this.messageIntegrity && this.confidentiality) {
            throw new IllegalStateException("Message Integrity was set "
                    + "to false but Confidentiality set to true.");
        }

        assertNotConnected();

        GSSContext context = null;
        
        try {
            byte[] data = null;
            
            SpnegoHttpURLConnection.LOCK.lock();
            try {
                // work-around to GSSContext/AD timestamp vs sequence field replay bug
                try { Thread.sleep(31); } catch (InterruptedException e) { assert true; }
                
                context = this.getGSSContext(url);
                context.requestMutualAuth(this.mutualAuth);
                context.requestConf(this.confidentiality);
                context.requestInteg(this.messageIntegrity);
                context.requestReplayDet(this.replayDetection);
                context.requestSequenceDet(this.sequenceDetection);
                context.requestCredDeleg(this.reqCredDeleg);
                
                data = context.initSecContext(EMPTY_BYTE, 0, 0);
            } finally {
                SpnegoHttpURLConnection.LOCK.unlock();
            }
            
            this.conn = (HttpURLConnection) url.openConnection();
            this.connected = true;

            final Set<String> keys = this.requestProperties.keySet();
            for (final String key : keys) {
                for (String value : this.requestProperties.get(key)) {
                    this.conn.addRequestProperty(key, value);
                }
            }

            this.conn.setInstanceFollowRedirects(false);
            this.conn.setRequestMethod(this.requestMethod);

            this.conn.setRequestProperty(Constants.AUTHZ_HEADER
                , Constants.NEGOTIATE_HEADER + ' ' + Base64.encodeBase64(data));

            if (null != dooutput && dooutput.size() > 0) {
                this.conn.setDoOutput(true);
                dooutput.writeTo(this.conn.getOutputStream());
            }

            this.conn.connect();
            
            // redirect if 302
            if (this.conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP 
                    && this.redirectCount < SpnegoHttpURLConnection.MAX_REDIRECTS) {
                
                return this.redirect(url, dooutput);
            }

            final SpnegoAuthScheme scheme = SpnegoProvider.getAuthScheme(
                    this.conn.getHeaderField(Constants.AUTHN_HEADER));
            
            // app servers will not return a WWW-Authenticate on 302, (and 30x...?)
            if (null == scheme) {
                log.debug("SpnegoProvider.getAuthScheme(...) returned null.");
                
            // client requesting to skip context loop if 200 and mutualAuth=false
            } else if (this.conn.getResponseCode() == HttpURLConnection.HTTP_OK
                    && !this.mutualAuth) {
                log.debug("SpnegoProvider.getAuthScheme(...) returned null.");
                
            } else {
                data = scheme.getToken();
    
                if (Constants.NEGOTIATE_HEADER.equalsIgnoreCase(scheme.getScheme())) {
                    SpnegoHttpURLConnection.LOCK.lock();
                    try {
                        data = context.initSecContext(data, 0, data.length);
                    } finally {
                        SpnegoHttpURLConnection.LOCK.unlock();
                    }

                    // TODO : support context loops where i>1
                    if (null != data) {
                        log.warn("Server requested context loop: " + data.length);
                    }
                    
                } else {
                    throw new UnsupportedOperationException("Scheme NOT Supported: " 
                            + scheme.getScheme());
                }

                this.cntxtEstablished = context.isEstablished();
            }
        } finally {
            this.dispose(context);
        }

        return this.conn;
    }

    /**
     * Logout the LoginContext instance, and call dispose() on GSSCredential 
     * if autoDisposeCreds is set to true, and call dispose on the passed-in 
     * GSSContext instance.
     *
     * @param context the context
     */
    private void dispose(final GSSContext context) {
        if (null != context) {
            try {
                SpnegoHttpURLConnection.LOCK.lock();
                try {
                    context.dispose();
                } finally {
                    SpnegoHttpURLConnection.LOCK.unlock();
                }
            } catch (GSSException gsse) {
                log.warn("call to dispose context failed.", gsse);
            }
        }
        
        if (null != this.credential && this.autoDisposeCreds) {
            try {
                this.credential.dispose();
            } catch (final GSSException gsse) {
                log.warn("call to dispose credential failed.", gsse);
            }
        }
        
        if (null != this.loginContext) {
            try {
                this.loginContext.logout();
            } catch (final LoginException lex) {
                log.warn("call to logout context failed.", lex);
            }
        }
    }

    /**
     * Logout and clear request properties.
     * 
     * @see java.net.HttpURLConnection#disconnect()
     */
    public void disconnect() {
        this.dispose(null);
        this.requestProperties.clear();
        this.connected = false;
        if (null != this.conn) {
            this.conn.disconnect();
        }
    }

    /**
     * Returns true if GSSContext has been established.
     * 
     * @return true if GSSContext has been established, false otherwise.
     */
    public boolean isContextEstablished() {
        return this.cntxtEstablished;
    }

    /**
     * Internal sanity check to validate not null key/value pairs.
     *
     * @param key the key
     * @param value the value
     */
    private void assertKeyValue(final String key, final String value) {
        if (null == key || key.isEmpty()) {
            throw new IllegalArgumentException("key parameter is null or empty");
        }
        if (null == value) {
            throw new IllegalArgumentException("value parameter is null");
        }
    }

    /**
     * Adds an HTTP Request property.
     * 
     * @param key request property name
     * @param value request propery value
     * @see java.net.URLConnection#addRequestProperty(String, String)
     */
    public void addRequestProperty(final String key, final String value) {
        assertNotConnected();
        assertKeyValue(key, value);

        if (this.requestProperties.containsKey(key)) {
            this.requestProperties.get(key).add(value);
        } else {
            setRequestProperty(key, value);
        }
    }

    /**
     * Sets an HTTP Request property.
     * 
     * @param key request property name
     * @param value request property value
     * @see java.net.URLConnection#setRequestProperty(String, String)
     */
    public void setRequestProperty(final String key, final String value) {
        assertNotConnected();
        assertKeyValue(key, value);

        final List<String> val = new ArrayList<String>();
        val.add(value);
        
        this.requestProperties.put(key, val);
    }
    
    /**
     * Returns a GSSContextt for the given url with a default lifetime.
     *  
     *
     * @param url http address
     * @return GSSContext for the given url
     * @throws GSSException the GSS exception
     * @throws PrivilegedActionException the privileged action exception
     */
    private GSSContext getGSSContext(final URL url) throws GSSException
        , PrivilegedActionException {

        if (null == this.credential) {
            if (null == this.loginContext) {
                throw new IllegalStateException(
                        "GSSCredential AND LoginContext NOT initialized");
                
            } else {
                this.credential = SpnegoProvider.getClientCredential(
                        this.loginContext.getSubject());
            }
        }
        
        return SpnegoProvider.getGSSContext(this.credential, url);
    }
    
    /**
     * Returns an error stream that reads from this open connection.
     *
     * @return error stream that reads from this open connection
     * @throws IOException Signals that an I/O exception has occurred.
     * @see java.net.HttpURLConnection#getErrorStream()
     */
    public InputStream getErrorStream() throws IOException {
        assertConnected();

        return this.conn.getInputStream();
    }

    /**
     * Get header value at specified index.
     *
     * @param index the index
     * @return header value at specified index
     */
    public String getHeaderField(final int index) {
        assertConnected();
        
        return this.conn.getHeaderField(index);
    }
    
    /**
     * Get header value by header name.
     * 
     * @param name name header
     * @return header value
     * @see java.net.HttpURLConnection#getHeaderField(String)
     */
    public String getHeaderField(final String name) {
        assertConnected();

        return this.conn.getHeaderField(name);
    }
    
    /**
     * Get header field key at specified index.
     *
     * @param index the index
     * @return header field key at specified index
     */
    public String getHeaderFieldKey(final int index) {
        assertConnected();
        
        return this.conn.getHeaderFieldKey(index);
    }

    /**
     * Gets the instance follow redirects.
     *
     * @return true if it should follow redirects
     * @see java.net.HttpURLConnection#getInstanceFollowRedirects()
     */
    public boolean getInstanceFollowRedirects() { // NOPMD
        return this.instanceFollowRedirects;
    }

    /**
     * Sets the instance follow redirects.
     *
     * @param followRedirects the new instance follow redirects
     * @see java.net.HttpURLConnection#setInstanceFollowRedirects(boolean)
     */
    public void setInstanceFollowRedirects(final boolean followRedirects) {
        assertNotConnected();

        this.instanceFollowRedirects = followRedirects;
    }

    /**
     * Returns an input stream that reads from this open connection.
     *
     * @return input stream that reads from this open connection
     * @throws IOException Signals that an I/O exception has occurred.
     * @see java.net.HttpURLConnection#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        assertConnected();

        return this.conn.getInputStream();
    }
    
    /**
     * Returns an output stream that writes to this open connection.
     *
     * @return output stream that writes to this connections
     * @throws IOException Signals that an I/O exception has occurred.
     * @see java.net.HttpURLConnection#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        assertConnected();
        
        return this.conn.getOutputStream();
    }

    /**
     * Returns HTTP Status code.
     *
     * @return HTTP Status Code
     * @throws IOException Signals that an I/O exception has occurred.
     * @see java.net.HttpURLConnection#getResponseCode()
     */
    public int getResponseCode() throws IOException {
        assertConnected();

        return this.conn.getResponseCode();
    }

    /**
     * Returns HTTP Status message.
     *
     * @return HTTP Status Message
     * @throws IOException Signals that an I/O exception has occurred.
     * @see java.net.HttpURLConnection#getResponseMessage()
     */
    public String getResponseMessage() throws IOException {
        assertConnected();

        return this.conn.getResponseMessage();
    }
    
    /**
     * Redirect.
     *
     * @param url the url
     * @param dooutput the dooutput
     * @return the http URL connection
     * @throws GSSException the GSS exception
     * @throws PrivilegedActionException the privileged action exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private HttpURLConnection redirect(final URL url, final ByteArrayOutputStream dooutput)
        throws GSSException, PrivilegedActionException, IOException {

        this.redirectCount++;
        final String location = this.getHeaderField("location");
        assert !location.isEmpty();
        
        if (!instanceFollowRedirects && '/' != location.charAt(0)) {
            final URL erl = new URL(location);
            if (!url.getHost().equalsIgnoreCase(erl.getHost()) 
                    || url.getPort() != erl.getPort()) {
                
                this.redirectCount = SpnegoHttpURLConnection.MAX_REDIRECTS;
                return this.conn;                
            }
        } 

        final List<String> cookies = 
            this.conn.getHeaderFields().get("Set-Cookie");

        this.conn.disconnect();
        this.connected = false;

        if (null != cookies) {
            this.requestProperties.remove("Cookie");
            for (String cookie : cookies) {
                this.addRequestProperty("Cookie", cookie);
            }
        }

        if ('/' == location.charAt(0)) {
            final String[] str = url.toString().split("/");
            final String newLocation = str[0] + "//" + str[2] + location;
            return this.connect(new URL(newLocation), dooutput);
        } else {
            return this.connect(new URL(location), dooutput);
        }
    }
    
    /**
     * Request that this GSSCredential be allowed for delegation.
     * 
     * @param requestDelegation true to allow/request delegation
     */
    public void requestCredDeleg(final boolean requestDelegation) {
        this.assertNotConnected();
        
        this.reqCredDeleg = requestDelegation;
    }
    
    /**
     * Specify if GSSContext should request Confidentiality.
     * Default is true.
     * 
     * @param confidential pass true for confidentiality
     */
    public void setConfidentiality(final boolean confidential) {
        this.confidentiality = confidential;
    }

    /**
     * Specify if GSSContext should request Message Integrity.
     * Default is true.
     * 
     * @param integrity pass true for message integrity
     */
    public void setMessageIntegrity(final boolean integrity) {
        this.messageIntegrity = integrity;
    }

    /**
     * Specify if GSSContext should request Mutual Auth.
     * Default is true.
     * 
     * @param mutual pass true for mutual authentication
     */
    public void setMutualAuth(final boolean mutual) {
        this.mutualAuth = mutual;
    }

    /**
     * Specify if if GSSContext should request should request Replay Detection.
     * Default is true.
     * 
     * @param replay pass true for replay detection
     */
    public void setReplayDetection(final boolean replay) {
        this.replayDetection = replay;
    }
    
    /**
     * May override the default GET method.
     *
     * @param method the new request method
     * @see java.net.HttpURLConnection#setRequestMethod(String)
     */
    public void setRequestMethod(final String method) {
        assertNotConnected();

        this.requestMethod = method;
    }
    
    /**
     * Specify if if GSSContext should request Sequence Detection.
     * Default is true.
     * 
     * @param sequence pass true for sequence detection
     */
    public void setSequenceDetection(final boolean sequence) {
        this.sequenceDetection = sequence;
    }
}
