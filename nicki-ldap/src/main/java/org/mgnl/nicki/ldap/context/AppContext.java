/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.context;

import java.util.Locale;

import org.mgnl.nicki.core.context.Context;
import org.mgnl.nicki.core.context.ThreadContext;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.context.NickiContext.READONLY;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public class AppContext {
    /**
     * Get the current context of this thread.
     */
    public static Context getInstance() {
        return ThreadContext.getInstance();
    }
    
	public static Object getRequest() {
		return getInstance().getRequest();
	}

	public static void setRequest(Object request) {
		getInstance().setRequest(request);
	}

	public static Object getResponse() {
		return getInstance().getResponse();
	}

	public static void setResponse(Object response) {
		getInstance().setResponse(response);
	}

	public static Locale getLocale() {
		return getInstance().getLocale();
	}

	public static void setLocale(Locale locale) {
		getInstance().setLocale(locale);
	}
	public static NickiContext getSystemContext(Target target, String username, String password) throws InvalidPrincipalException {
		DynamicObject user = target.login(new NickiPrincipal(username, password));
		return getSystemContext(target, user, READONLY.FALSE);
	}

	public static NickiContext getSystemContext(String username, String password) throws InvalidPrincipalException {
		DynamicObject user = TargetFactory.getDefaultTarget().login(new NickiPrincipal(username, password));
		return getSystemContext(TargetFactory.getDefaultTarget(), user, READONLY.FALSE);
	}

	public static NickiContext getSystemContext() throws InvalidPrincipalException {
		return getSystemContext(TargetFactory.getDefaultTarget().getName());
	}

	public static NickiContext getSystemContext(String targetName) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		new NickiPrincipal(target.getProperty("securityPrincipal"),
				target.getProperty("securityCredentials"));
		DynamicObject user = target.login(new NickiPrincipal(target.getProperty("securityPrincipal"),
				target.getProperty("securityCredentials")));
		return getSystemContext(target, user, READONLY.TRUE);
	}

	private static NickiContext getSystemContext(Target target, DynamicObject user, READONLY readonly) throws InvalidPrincipalException {
		if (target != null && user != null) {
			return new SystemContext(target, user, readonly);
		}
		return null;
	}

	public static NickiContext getNamedUserContext(String targetName, DynamicObject user, String password) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		if (target != null) {
			return new NamedUserContext(target, user, password);
		}
		return null;
	}
	
	public static NickiContext getGuestContext(String targetName) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		if (target != null) {
			return new GuestContext(target, NickiContext.READONLY.TRUE);
		}
		return null;
	}
	
}
