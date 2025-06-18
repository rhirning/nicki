
package org.mgnl.nicki.portlets;

/*-
 * #%L
 * nicki-portlets
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


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.auth.NickiAdapterLoginModule;
import org.mgnl.nicki.core.auth.SSOAdapter;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.util.XmlHelper;
import org.w3c.dom.Document;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class IframeAdapter.
 */
@Slf4j
public class IframeAdapter implements SSOAdapter {
	
	/** The Constant USER_PATH. */
	static final String USER_PATH = "/Assertion/AuthenticationStatement/Subject/NameIdentifier";
	
	/** The is init. */
	private boolean isInit = false;
	
	/** The type. */
	private TYPE type = TYPE.UNKNOWN;
	
	/** The request. */
	private Object request;
	
	/** The password. */
	private String password;
	
	/** The name. */
	private String name;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public TYPE getType() {
		return type;
	}

	/**
	 * Inits the.
	 */
	public void init() {
		if (!isInit) {
			String encodedToken = null;
			String encodedPassword = null;
			String encodedName = null;
			if (AppContext.getRequestParameters() != null) {
				encodedToken = AppContext.getRequestParameters().get("nickiToken");
				encodedPassword = getRequest(AppContext.getRequest()).getParameter("nickiPassword");
				encodedName = getRequest(AppContext.getRequest()).getParameter("nickiName");
			} else {
				encodedToken = getRequest(AppContext.getRequest()).getParameter("nickiToken");
				encodedPassword = getRequest(AppContext.getRequest()).getParameter("nickiPassword");
				encodedName = getRequest(AppContext.getRequest()).getParameter("nickiName");
			}
			log.debug("encodedToken=" + encodedToken);
			log.debug("encodedPassword=" + encodedPassword);
			if (StringUtils.isNotBlank(encodedToken)) {
				type = TYPE.SAML;
				try {
					password = new String(Base64.decodeBase64(encodedToken.getBytes()), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					password = new String(Base64.decodeBase64(encodedPassword.getBytes()));
					log.debug("Could use charset UTF-8");
				}
				name = getNameFromToken(password);
			} else if (StringUtils.isNotBlank(encodedPassword)) {
				type = TYPE.BASIC;
				password = new String(Base64.decodeBase64(encodedPassword.getBytes()));
				log.debug("encodedName=" + encodedName);
				name = new String(Base64.decodeBase64(encodedName.getBytes()));
			} else {
				type = TYPE.UNKNOWN;
				password = "";
			}
			isInit = true;
			log.debug(toString());
		}
	}

	/**
	 * Gets the name from token.
	 *
	 * @param token the token
	 * @return the name from token
	 */
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

	/**
	 * Gets the groups.
	 *
	 * @param request the request
	 * @return the groups
	 */
	public  List<String> getGroups(Object request) {
		List<String> list = new ArrayList<String>();
		return list;
	}
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public char[] getPassword() {
		init();
		return password.toCharArray();
	}
	
	/**
	 * user DN like 
	 * cn=padmin,ou=users,o=utopia.
	 *
	 * @return the name
	 */
	public String getName() {
		init();
		return name;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
		init();
		String userDn = getName();
		if (StringUtils.isNotEmpty(userDn)) {
			userDn = StringUtils.substringAfter(userDn, "=");
			userDn = StringUtils.substringBefore(userDn, ",");
			userDn = StringUtils.strip(userDn);
		} else {
			userDn = null;
		}
		return userDn;
	}

	
	/**
	 * Checks if is in group.
	 *
	 * @param request the request
	 * @param group the group
	 * @return true, if is in group
	 */
	public boolean isInGroup(Object request, String group) {
		return getGroups(request).contains(group);
	}
	
	/**
	 * Gets the request.
	 *
	 * @param request the request
	 * @return the request
	 */
	private HttpServletRequest getRequest(Object request) {
		return (HttpServletRequest) request;
	}
	
	/**
	 * Gets the session.
	 *
	 * @param request the request
	 * @return the session
	 */
	private HttpSession getSession(Object request) {
		return getRequest(request).getSession(true);
	}

	/**
	 * Gets the attribute from request.
	 *
	 * @param request the request
	 * @param key the key
	 * @return the attribute from request
	 */
	public Object getAttributeFromRequest(Object request, String key) {
		return getRequest(request).getAttribute(key);
	}


	/**
	 * Gets the attribute from session.
	 *
	 * @param request the request
	 * @param key the key
	 * @return the attribute from session
	 */
	public Object getAttributeFromSession(Object request, String key) {
		return getSession(request).getAttribute(key);
	}


	/**
	 * Sets the attribute in request.
	 *
	 * @param request the request
	 * @param key the key
	 * @param object the object
	 */
	public void setAttributeInRequest(Object request, String key, Object object) {
		getRequest(request).setAttribute(key, object);
	}


	/**
	 * Sets the attribute in session.
	 *
	 * @param request the request
	 * @param key the key
	 * @param object the object
	 */
	public void setAttributeInSession(Object request, String key, Object object) {
		getSession(request).setAttribute(key, object);
	}


	/**
	 * Gets the session id.
	 *
	 * @param request the request
	 * @return the session id
	 */
	public String getSessionId(Object request) {
		return getSession(request).getId();
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IframeAdapter: type=").append(type).append("|name=").append(name);
		sb.append("|password=").append(password);
		return sb.toString();
	}

	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	@Override
	public void setRequest(Object request) {
		this.request = request;
	}
	
	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public Object getRequest() {
		if (this.request != null) {
			return this.request;
		} else {
			return AppContext.getRequest();
		}
	}

	/**
	 * Inits the.
	 *
	 * @param loginModule the login module
	 */
	@Override
	public void init(NickiAdapterLoginModule loginModule) {
		// TODO Auto-generated method stub
		
	}
	
	
}
