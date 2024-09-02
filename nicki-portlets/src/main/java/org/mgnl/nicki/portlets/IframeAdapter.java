
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

@Slf4j
public class IframeAdapter implements SSOAdapter {
	static final String USER_PATH = "/Assertion/AuthenticationStatement/Subject/NameIdentifier";
	private boolean isInit = false;
	private TYPE type = TYPE.UNKNOWN;
	
	private Object request;
	
	private String password;
	private String name;

	@Override
	public TYPE getType() {
		return type;
	}

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
	
	public char[] getPassword() {
		init();
		return password.toCharArray();
	}
	/** user DN like 
	 * cn=padmin,ou=users,o=utopia
	 */
	public String getName() {
		init();
		return name;
	}

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
		StringBuilder sb = new StringBuilder();
		sb.append("IframeAdapter: type=").append(type).append("|name=").append(name);
		sb.append("|password=").append(password);
		return sb.toString();
	}

	@Override
	public void setRequest(Object request) {
		this.request = request;
	}
	
	public Object getRequest() {
		if (this.request != null) {
			return this.request;
		} else {
			return AppContext.getRequest();
		}
	}

	@Override
	public void init(NickiAdapterLoginModule loginModule) {
		// TODO Auto-generated method stub
		
	}
	
	
}
