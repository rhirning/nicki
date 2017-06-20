package org.mgnl.nicki.spnego;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    	System.out.println("init " + getClass().getName());

    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response
        , final FilterChain chain) throws IOException, ServletException {
    	

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        List<String> headerNames = new ArrayList<>();
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
