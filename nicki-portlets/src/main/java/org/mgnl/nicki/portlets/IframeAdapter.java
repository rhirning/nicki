/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.portlets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.SSOAdapter;
import org.mgnl.nicki.core.util.XmlHelper;
import org.w3c.dom.Document;


public class IframeAdapter implements SSOAdapter {
	static final String USER_PATH = "/Assertion/AuthenticationStatement/Subject/NameIdentifier";
	private boolean isInit = false;
	private TYPE type = TYPE.UNKNOWN;
	
	private String password;
	private String name;

	@Override
	public TYPE getType() {
		return type;
	}

	@Override
	public void init(Object request) {
		if (!isInit) {
			String encodedToken = getRequest(request).getParameter("nickiToken");
			System.out.println("encodedToken=" + encodedToken);
			String encodedPassword = getRequest(request).getParameter("nickiPassword");
			System.out.println("encodedPassword=" + encodedPassword);
			String encoded;
			if (StringUtils.isNotBlank(encodedToken)) {
				type = TYPE.SAML;
				encoded = getRequest(request).getParameter("nickiToken");
				password = new String(Base64.decodeBase64(encoded.getBytes()));
				name = getNameFromToken(password);
			} else if (StringUtils.isNotBlank(encodedPassword)) {
				type = TYPE.BASIC;
				encoded = getRequest(request).getParameter("nickiPassword");
				password = new String(Base64.decodeBase64(encoded.getBytes()));
				String encodedName = getRequest(request).getParameter("nickiName");
				System.out.println("encodedName=" + encodedName);
				name = new String(Base64.decodeBase64(encodedName.getBytes()));
			} else {
				type = TYPE.UNKNOWN;
				password = "";
			}
			isInit = true;
		}
		System.out.println(toString());
	}

	private String getNameFromToken(String token) {
		try {
			Document doc = XmlHelper.getDocumentFromXml(token);
			XPath xPath = XPathFactory.newInstance().newXPath();
			return xPath.compile(USER_PATH).evaluate(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public  List<String> getGroups(Object request) {
		List<String> list = new ArrayList<String>();
		return list;
	}
	
	public char[] getPassword(Object request) {
		init(request);
		return password.toCharArray();
	}
	/** user DN like 
	 * cn=padmin,ou=users,o=utopia
	 */
	public String getName(Object request) {
		init(request);
		return name;
	}

	public String getUserId(Object request) {
		init(request);
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

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("IframeAdapter: type=").append(type).append("|name=").append(name);
		sb.append("|password=").append(password);
		return sb.toString();
	}
	
	
}
