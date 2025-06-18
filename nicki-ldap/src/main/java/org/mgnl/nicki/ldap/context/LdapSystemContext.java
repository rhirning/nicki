
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


import java.io.Serializable;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;


/**
 * The Class LdapSystemContext.
 */
@SuppressWarnings("serial")
public final class LdapSystemContext extends LdapContext implements NickiContext, Serializable {

	/**
	 * Instantiates a new ldap system context.
	 *
	 * @param adapter the adapter
	 * @param target the target
	 * @param user the user
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	protected LdapSystemContext(DynamicObjectAdapter adapter, Target target, DynamicObject user) throws InvalidPrincipalException {
		super(adapter, target, READONLY.FALSE);
		setPrincipal(new NickiPrincipal(getTarget().getProperty("securityPrincipal"),
				getTarget().getProperty("securityCredentials")));
		setUser(user);
	}

}
