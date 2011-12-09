/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.novell.userapp.jaas;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.auth.SSOAdapter;


public class IframeAdapter implements SSOAdapter {

	public  List<String> getGroups(Object request) {
		List<String> list = new ArrayList<String>();
		return list;
	}
	
	public char[] getPassword(Object request) {
		String encodedPassword = getRequest(request).getParameter("nickiPassword");
		return new String(Base64.decodeBase64(encodedPassword.getBytes())).toCharArray();
	}
	/** user DN like 
	 * cn=padmin,ou=users,o=utopia
	 */
	public String getName(Object request) {
		String encodedName = getRequest(request).getParameter("nickiName");
		return new String(Base64.decodeBase64(encodedName.getBytes()));
	}

	public String getUserId(Object request) {
		String userDn = getName(request);
		if (StringUtils.isNotEmpty(userDn)) {
			userDn = StringUtils.substringAfter(userDn, "=");
			userDn = StringUtils.substringBefore(userDn, ",");
			userDn = StringUtils.strip(userDn);
		} else {
			userDn = null;
		}
		return userDn;
	}

	
	public boolean isInGroup(Object request, String group) {
		return getGroups(request).contains(group);
	}
	
	private HttpServletRequest getRequest(Object request) {
		return (HttpServletRequest) request;
	}
	private HttpSession getSession(Object request) {
		return getRequest(request).getSession(true);
	}

	public Object getAttributeFromRequest(Object request, String key) {
		return getRequest(request).getAttribute(key);
	}


	public Object getAttributeFromSession(Object request, String key) {
		return getSession(request).getAttribute(key);
	}


	public void setAttributeInRequest(Object request, String key, Object object) {
		getRequest(request).setAttribute(key, object);
	}


	public void setAttributeInSession(Object request, String key, Object object) {
		getSession(request).setAttribute(key, object);
	}


	public String getSessionId(Object request) {
		return getSession(request).getId();
	}
	
	
}
