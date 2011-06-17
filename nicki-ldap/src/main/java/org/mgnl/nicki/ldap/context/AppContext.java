package org.mgnl.nicki.ldap.context;

import java.util.Locale;

import org.mgnl.nicki.core.context.Context;
import org.mgnl.nicki.core.context.ThreadContext;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.NickiContext.READONLY;

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
	public static NickiContext getSystemContext() throws InvalidPrincipalException {
		return getSystemContext(TargetFactory.getDefaultTarget(), READONLY.TRUE);
	}

	protected static NickiContext getSystemContext(READONLY readonly) throws InvalidPrincipalException {
		return getSystemContext(TargetFactory.getDefaultTarget(), readonly);
	}

	public static NickiContext getSystemContext(String targetName) throws InvalidPrincipalException {
		return getSystemContext(TargetFactory.getTarget(targetName), READONLY.TRUE);
	}

	protected static NickiContext getSystemContext(Target target, READONLY readonly) throws InvalidPrincipalException {
		if (target != null) {
			return new SystemContext(target, readonly);
		}
		return null;
	}

	public static NickiContext getNamedUserContext(String targetName, String user, String password) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		if (target != null) {
			return new NamedUserContext(target, user, password, NickiContext.READONLY.TRUE);
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
