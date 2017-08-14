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
package org.mgnl.nicki.core.context;

import java.util.Locale;
import java.util.Map;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.objects.DynamicObject;

public class AppContext {
    /**
     * Get the current context of this thread.
     */
    public static Context getInstance() {
        return ThreadContext.getInstance();
    }
    
    public static void setRequestParameters(Map<String, String> map) {
    	getInstance().setRequestParameters(map);
    }
	
	public static Map<String, String> getRequestParameters() {
		return getInstance().getRequestParameters();
	}
    
	public static Object getRequest() {
		return getInstance().getRequest();
	}

	public static void setRequest(Object request) {
		getInstance().setRequest(request);
	}

	public static Locale getLocale() {
		return getInstance().getLocale();
	}

	public static void setUser(DynamicObject user) {
		getInstance().setUser(user);
	}

	public static DynamicObject getUser() {
		return getInstance().getUser();
	}

	public static void setCredentials(byte[] credentials) {
		getInstance().setCredentials(credentials);
	}

	public static byte[] getCredentials() {
		return getInstance().getCredentials();
	}

	public static void setLocale(Locale locale) {
		getInstance().setLocale(locale);
	}
	public static NickiContext getSystemContext(Target target, String username, String password) throws InvalidPrincipalException {
		DynamicObject user = target.login(new NickiPrincipal(username, password));
		return getSystemContext(target, user);
	}

	public static NickiContext getSystemContext(String username, String password) throws InvalidPrincipalException {
		DynamicObject user = TargetFactory.getDefaultTarget().login(new NickiPrincipal(username, password));
		return getSystemContext(TargetFactory.getDefaultTarget(), user);
	}

	public static NickiContext getSystemContext() throws InvalidPrincipalException {
		return getSystemContext(TargetFactory.getDefaultTarget().getName());
	}

	public static NickiContext getSystemContext(String targetName) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		if (target != null) {
			new NickiPrincipal(target.getProperty("securityPrincipal"),
					target.getProperty("securityCredentials"));
			DynamicObject user = target.login(new NickiPrincipal(target.getProperty("securityPrincipal"),
					target.getProperty("securityCredentials")));
			return getSystemContext(target, user);
		} else {
			return null;
		}
	}

	private static NickiContext getSystemContext(Target target, DynamicObject user) throws InvalidPrincipalException {
		if (target != null && user != null) {
			return target.getSystemContext(user);
		}
		return null;
	}

	public static NickiContext getNamedUserContext(String targetName, DynamicObject user, String password) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		if (target != null) {
			return target.getNamedUserContext(user, password);
		}
		return null;
	}
	
	public static NickiContext getGuestContext(String targetName) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		if (target != null) {
			return target.getGuestContext();
		}
		return null;
	}
	
}
