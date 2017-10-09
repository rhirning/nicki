
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
