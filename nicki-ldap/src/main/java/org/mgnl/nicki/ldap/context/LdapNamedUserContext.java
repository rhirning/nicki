
package org.mgnl.nicki.ldap.context;

/*-
 * #%L
 * nicki-ldap
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


import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;


/**
 * The Class LdapNamedUserContext.
 */
@SuppressWarnings("serial")
public class LdapNamedUserContext extends LdapContext implements NickiContext {

	/**
	 * Instantiates a new ldap named user context.
	 *
	 * @param adapter the adapter
	 * @param target the target
	 * @param user the user
	 * @param password the password
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public LdapNamedUserContext(DynamicObjectAdapter adapter, Target target, DynamicObject user, String password) throws InvalidPrincipalException {
		super(adapter, target, READONLY.FALSE);
		setPrincipal(new NickiPrincipal(user.getPath(), password));
		setUser(user);
	}
}
