
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
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.NickiAdapterLoginModule;
import org.mgnl.nicki.core.auth.SSOAdapter;
import org.mgnl.nicki.core.context.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sssw.fw.directory.api.EbiRealmGroup;
import com.sssw.fw.directory.api.EbiRealmUser;
import com.sssw.fw.directory.client.EboDirectoryHelper;
import com.sssw.fw.directory.core.EboUserCredentials;
import com.sssw.fw.exception.EboUnrecoverableSystemException;
import com.sssw.portal.api.EbiPortalContext;


public class UserAppAdapter implements SSOAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(UserAppAdapter.class);

	static final String BASE_KEY = "com.sssw.fw.directory.realm.impl.jndildap.EboJndiLdapUserConnectionInfoHelper:";
	static final String USER_CREDENTIALS = BASE_KEY + "USER_CREDENTIALS";
	static final String USER_CONNECTION = BASE_KEY + "USER_DIRECTORY_CONNECTION";
	
	public static final String ATTRIBUTE_NAME_PORTAL_CONTEXT = "com.sssw.portal.api.EbiPortalContext";
	private TYPE type = TYPE.UNKNOWN;

	private Object request;
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
	
	public  List<String> getGroups() {
		List<String> list = new ArrayList<String>();
		try {
			EbiRealmUser user = EboDirectoryHelper.getEbiRealmUser(getContext());
			Set<EbiRealmGroup> groups = user.getGroups();
			for (EbiRealmGroup group : groups) {
				list.add(group.getName());
			}
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return list;
	}
	

	private EboUserCredentials getUserCredentials() {
		return ((EboUserCredentials)getContext().getValue(USER_CREDENTIALS));
	}
	
	public char[] getPassword() {
		EboUserCredentials credentials = getUserCredentials();
		return decrypt(credentials).toCharArray();
	}
	
	private String decrypt(EboUserCredentials credentials) {
		Class<?> c = credentials.getClass();
		try {
			Method m = c.getDeclaredMethod("decrypt", new Class[]{String.class});
			m.setAccessible(true);
			return (String) m.invoke(credentials, new Object[]{credentials.getEncPassword()});
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return null;
	}

	/** user DN like 
	 * cn=padmin,ou=users,o=utopia
	 */
	public String getName() {
		String userName = null;
		try {
			userName = EboDirectoryHelper.getUserID(getContext());
		} catch (EboUnrecoverableSystemException e) {
			LOG.error("Error", e);
		}
		return userName;
	}

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
	
	public boolean isInGroup(Object request, String group) {
		return getGroups().contains(group);
	}
	
	private EbiPortalContext getContext() {
		PortletRequest pRequest = (PortletRequest) getRequest();
		return (EbiPortalContext) pRequest.getAttribute(ATTRIBUTE_NAME_PORTAL_CONTEXT);
	}

	private PortletRequest getRequest(Object request) {
		return (PortletRequest) request;
	}
	private PortletSession getSession() {
		return getRequest(getRequest()).getPortletSession(true);
	}

	public Object getAttributeFromRequest(String key) {
		return getRequest(getRequest()).getAttribute(key);
	}


	public Object getAttributeFromSession(String key) {
		return getSession().getAttribute(key);
	}


	public void setAttributeInRequest(String key, Object object) {
		getRequest(getRequest()).setAttribute(key, object);
	}


	public void setAttributeInSession(String key, Object object) {
		getSession().setAttribute(key, object);
	}


	public String getSessionId() {
		return getSession().getId();
	}

	@Override
	public TYPE getType() {
		init();
		return type;
	}

	@Override
	public void init(NickiAdapterLoginModule loginModule) {
		// TODO Auto-generated method stub
		
	}
	
	
}
