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
package org.mgnl.nicki.idm.novell.jaas;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.SSOAdapter;

import com.sssw.fw.directory.api.EbiRealmGroup;
import com.sssw.fw.directory.api.EbiRealmUser;
import com.sssw.fw.directory.client.EboDirectoryHelper;
import com.sssw.fw.directory.core.EboUserCredentials;
import com.sssw.fw.exception.EboUnrecoverableSystemException;
import com.sssw.portal.api.EbiPortalContext;


public class UserAppAdapter implements SSOAdapter {

	static String BASE_KEY = "com.sssw.fw.directory.realm.impl.jndildap.EboJndiLdapUserConnectionInfoHelper:";
	static String USER_CREDENTIALS = BASE_KEY + "USER_CREDENTIALS";
	static String USER_CONNECTION = BASE_KEY + "USER_DIRECTORY_CONNECTION";
	
	public static final String ATTRIBUTE_NAME_PORTAL_CONTEXT = "com.sssw.portal.api.EbiPortalContext";
	public  List<String> getGroups(Object request) {
		List<String> list = new ArrayList<String>();
		try {
			EbiRealmUser user = EboDirectoryHelper.getEbiRealmUser(getContext(request));
			Set<EbiRealmGroup> groups = user.getGroups();
			for (EbiRealmGroup group : groups) {
				list.add(group.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

	private EboUserCredentials getUserCredentials(Object request) {
		return ((EboUserCredentials)getContext(request).getValue(USER_CREDENTIALS));
	}
	
	public char[] getPassword(Object request) {
		EboUserCredentials credentials = getUserCredentials(request);
		return decrypt(credentials).toCharArray();
	}
	
	private String decrypt(EboUserCredentials credentials) {
		Class<?> c = credentials.getClass();
		try {
			Method m = c.getDeclaredMethod("decrypt", new Class[]{String.class});
			m.setAccessible(true);
			return (String) m.invoke(credentials, new Object[]{credentials.getEncPassword()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** user DN like 
	 * cn=padmin,ou=users,o=utopia
	 */
	public String getName(Object request) {
		String userName = null;
		try {
			userName = EboDirectoryHelper.getUserID(getContext(request));
		} catch (EboUnrecoverableSystemException e) {
			e.printStackTrace();
		}
		return userName;
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

	// TODO
	/*
	public boolean isAdmin(Object request) {
		String adminGroup = Config.getInstance().getAdminGroup();
		return isInGroup(request, adminGroup);
	}
	 */
	
	public boolean isInGroup(Object request, String group) {
		return getGroups(request).contains(group);
	}
	
	private EbiPortalContext getContext(Object request) {
		PortletRequest pRequest = (PortletRequest) request;
		return (EbiPortalContext) pRequest.getAttribute(ATTRIBUTE_NAME_PORTAL_CONTEXT);
	}

	private PortletRequest getRequest(Object request) {
		return (PortletRequest) request;
	}
	private PortletSession getSession(Object request) {
		return getRequest(request).getPortletSession(true);
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
