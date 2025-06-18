
package org.mgnl.nicki.core.context;

/*-
 * #%L
 * nicki-core
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


import java.util.Locale;
import java.util.Map;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.objects.DynamicObject;


/**
 * The Class AppContext.
 */
public class AppContext {
    
    /**
     * Get the current context of this thread.
     *
     * @return single instance of AppContext
     */
    public static Context getInstance() {
        return ThreadContext.getInstance();
    }
    
    /**
     * Sets the request parameters.
     *
     * @param map the map
     */
    public static void setRequestParameters(Map<String, String> map) {
    	getInstance().setRequestParameters(map);
    }
	
	/**
	 * Gets the request parameters.
	 *
	 * @return the request parameters
	 */
	public static Map<String, String> getRequestParameters() {
		return getInstance().getRequestParameters();
	}
    
	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public static synchronized Object getRequest() {
		return getInstance().getRequest();
	}

	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	public static synchronized void setRequest(Object request) {
		getInstance().setRequest(request);
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public static Locale getLocale() {
		return getInstance().getLocale();
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the new locale
	 */
	public static void setLocale(Locale locale) {
		getInstance().setLocale(locale);
	}
	
	/**
	 * Gets the system context.
	 *
	 * @param target the target
	 * @param username the username
	 * @param password the password
	 * @return the system context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public static NickiContext getSystemContext(Target target, String username, String password) throws InvalidPrincipalException {
		DynamicObject user = target.login(new NickiPrincipal(username, password));
		return getSystemContext(target, user);
	}

	/**
	 * Gets the system context.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the system context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public static NickiContext getSystemContext(String username, String password) throws InvalidPrincipalException {
		DynamicObject user = TargetFactory.getDefaultTarget().login(new NickiPrincipal(username, password));
		return getSystemContext(TargetFactory.getDefaultTarget(), user);
	}

	/**
	 * Gets the system context.
	 *
	 * @return the system context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public static NickiContext getSystemContext() throws InvalidPrincipalException {
		return getSystemContext(TargetFactory.getDefaultTarget().getName());
	}

	/**
	 * Gets the system context.
	 *
	 * @param targetName the target name
	 * @return the system context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
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

	/**
	 * Gets the system context.
	 *
	 * @param targetName the target name
	 * @param user the user
	 * @return the system context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public static NickiContext getSystemContext(String targetName, DynamicObject user) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		return getSystemContext(target, user);
	}

	/**
	 * Gets the system context.
	 *
	 * @param target the target
	 * @param user the user
	 * @return the system context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	private static NickiContext getSystemContext(Target target, DynamicObject user) throws InvalidPrincipalException {
		if (target != null && user != null) {
			return target.getSystemContext(user);
		}
		return null;
	}

	/**
	 * Gets the named user context.
	 *
	 * @param targetName the target name
	 * @param user the user
	 * @param password the password
	 * @return the named user context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public static NickiContext getNamedUserContext(String targetName, DynamicObject user, String password) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		if (target != null) {
			return target.getNamedUserContext(user, password);
		}
		return null;
	}
	
	/**
	 * Gets the guest context.
	 *
	 * @param targetName the target name
	 * @return the guest context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public static NickiContext getGuestContext(String targetName) throws InvalidPrincipalException {
		Target target = TargetFactory.getTarget(targetName);
		if (target != null) {
			return target.getGuestContext();
		}
		return null;
	}
	
}
