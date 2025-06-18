
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
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * The Class LogFilter.
 */
public final class LogFilter implements Filter {
    
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
     * HTTP Response Header <b>Negotiate</b>. 
     * 
     * <p>The filter will set this as the value for the "WWW-Authenticate" 
     * header. Note that the filter may also add another header with 
     * a value of "Basic" (if allowed by the web.xml file).</p>
     */
    public static final String NEGOTIATE_HEADER = "Negotiate";

    /**
     * Inits the.
     *
     * @param filterConfig the filter config
     * @throws ServletException the servlet exception
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    	System.out.println("init " + getClass().getName());

    }

    /**
     * Destroy.
     */
    @Override
    public void destroy() {
    }

    /**
     * Do filter.
     *
     * @param request the request
     * @param response the response
     * @param chain the chain
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response
        , final FilterChain chain) throws IOException, ServletException {
    	

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        List<String> headerNames = new ArrayList<>();
        @SuppressWarnings("rawtypes")
		Enumeration enumeration = httpRequest.getHeaderNames();
        boolean hasAuthorization = false;
        while (enumeration.hasMoreElements()) {
        	String headerName = (String) enumeration.nextElement();
        	if (AUTHZ_HEADER.equalsIgnoreCase(headerName)) {
        		hasAuthorization = true;
        	}
        	headerNames.add( headerName);
        }
    	System.out.println("HeaderNames: " + headerNames);
    	if (!hasAuthorization) {
        	System.out.println("Header authorization missing. Sending request");
        	httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           	httpServletResponse.setContentLength(0);
           	httpServletResponse.flushBuffer();
        	httpServletResponse.setHeader(AUTHN_HEADER, NEGOTIATE_HEADER);
        	return;
    	} else {
        	System.out.println("Header authorization: " + httpRequest.getHeader(AUTHZ_HEADER));
    	}

        chain.doFilter(request, response);
    }
}
