
package org.mgnl.nicki.idm.novell.jaas;

/*-
 * #%L
 * nicki-idm-novell
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


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.auth.NickiAdapterLoginModule;
import org.mgnl.nicki.core.auth.SSOAdapter;
import org.mgnl.nicki.core.context.AppContext;

import com.sssw.fw.directory.api.EbiRealmGroup;
import com.sssw.fw.directory.api.EbiRealmUser;
import com.sssw.fw.directory.client.EboDirectoryHelper;
import com.sssw.fw.directory.core.EboUserCredentials;
import com.sssw.fw.exception.EboUnrecoverableSystemException;
import com.sssw.portal.api.EbiPortalContext;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class UserAppAdapter.
 */
@Slf4j
public class UserAppAdapter implements SSOAdapter {

	/** The Constant BASE_KEY. */
	static final String BASE_KEY = "com.sssw.fw.directory.realm.impl.jndildap.EboJndiLdapUserConnectionInfoHelper:";
	
	/** The Constant USER_CREDENTIALS. */
	static final String USER_CREDENTIALS = BASE_KEY + "USER_CREDENTIALS";
	
	/** The Constant USER_CONNECTION. */
	static final String USER_CONNECTION = BASE_KEY + "USER_DIRECTORY_CONNECTION";
	
	/** The Constant ATTRIBUTE_NAME_PORTAL_CONTEXT. */
	public static final String ATTRIBUTE_NAME_PORTAL_CONTEXT = "com.sssw.portal.api.EbiPortalContext";
	
	/** The type. */
	private TYPE type = TYPE.UNKNOWN;

	/** The request. */
	private Object request;
	
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
	 */
	public void init() {
		String credentials = new String(getPassword());
		if (StringUtils.length(credentials) > 100 &&
				  Base64.isBase64(credentials.getBytes())) {
			type = TYPE.SAML;
		} else if (StringUtils.length(credentials) > 0) {
			type = TYPE.BASIC;
		} else {
			type = TYPE.UNKNOWN;
		}
	}
	
	/**
	 * Gets the groups.
	 *
	 * @return the groups
	 */
	public  List<String> getGroups() {
		List<String> list = new ArrayList<String>();
		try {
			EbiRealmUser user = EboDirectoryHelper.getEbiRealmUser(getContext());
			Set<EbiRealmGroup> groups = user.getGroups();
			for (EbiRealmGroup group : groups) {
				list.add(group.getName());
			}
		} catch (Exception e) {
			log.error("Error", e);
		}
		return list;
	}
	

	/**
	 * Gets the user credentials.
	 *
	 * @return the user credentials
	 */
	private EboUserCredentials getUserCredentials() {
		return ((EboUserCredentials)getContext().getValue(USER_CREDENTIALS));
	}
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public char[] getPassword() {
		EboUserCredentials credentials = getUserCredentials();
		return decrypt(credentials).toCharArray();
	}
	
	/**
	 * Decrypt.
	 *
	 * @param credentials the credentials
	 * @return the string
	 */
	private String decrypt(EboUserCredentials credentials) {
		Class<?> c = credentials.getClass();
		try {
			Method m = c.getDeclaredMethod("decrypt", new Class[]{String.class});
			m.setAccessible(true);
			return (String) m.invoke(credentials, new Object[]{credentials.getEncPassword()});
		} catch (Exception e) {
			log.error("Error", e);
		}
		return null;
	}

	/**
	 * user DN like 
	 * cn=padmin,ou=users,o=utopia.
	 *
	 * @return the name
	 */
	public String getName() {
		String userName = null;
		try {
			userName = EboDirectoryHelper.getUserID(getContext());
		} catch (EboUnrecoverableSystemException e) {
			log.error("Error", e);
		}
		return userName;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
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
		return getGroups().contains(group);
	}
	
	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	private EbiPortalContext getContext() {
		PortletRequest pRequest = (PortletRequest) getRequest();
		return (EbiPortalContext) pRequest.getAttribute(ATTRIBUTE_NAME_PORTAL_CONTEXT);
	}

	/**
	 * Gets the request.
	 *
	 * @param request the request
	 * @return the request
	 */
	private PortletRequest getRequest(Object request) {
		return (PortletRequest) request;
	}
	
	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	private PortletSession getSession() {
		return getRequest(getRequest()).getPortletSession(true);
	}

	/**
	 * Gets the attribute from request.
	 *
	 * @param key the key
	 * @return the attribute from request
	 */
	public Object getAttributeFromRequest(String key) {
		return getRequest(getRequest()).getAttribute(key);
	}


	/**
	 * Gets the attribute from session.
	 *
	 * @param key the key
	 * @return the attribute from session
	 */
	public Object getAttributeFromSession(String key) {
		return getSession().getAttribute(key);
	}


	/**
	 * Sets the attribute in request.
	 *
	 * @param key the key
	 * @param object the object
	 */
	public void setAttributeInRequest(String key, Object object) {
		getRequest(getRequest()).setAttribute(key, object);
	}


	/**
	 * Sets the attribute in session.
	 *
	 * @param key the key
	 * @param object the object
	 */
	public void setAttributeInSession(String key, Object object) {
		getSession().setAttribute(key, object);
	}


	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public String getSessionId() {
		return getSession().getId();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public TYPE getType() {
		init();
		return type;
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
