/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.ldap.context;

import java.io.Serializable;

import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public final class SystemContext extends BasicContext implements NickiContext, Serializable {

	protected SystemContext(Target target, DynamicObject user, READONLY readonly) throws InvalidPrincipalException {
		super(target, readonly);
		setPrincipal(new NickiPrincipal(getTarget().getProperty("securityPrincipal"),
				getTarget().getProperty("securityCredentials")));
		setUser(user);
	}

}