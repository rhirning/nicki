/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.ldap.context;

import java.io.Serializable;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.context.ContextFactory;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;
import org.mgnl.nicki.ldap.objects.DynamicObjectLdapAdapter;

public class LdapContextFactory implements ContextFactory, Serializable {

	private static final long serialVersionUID = -5770837695968996467L;
	private static final DynamicObjectAdapter ADAPTER = new DynamicObjectLdapAdapter(); 
	
	@Override
	public NickiContext getGuestContext(Target target) {
		return new LdapGuestContext(getAdapter(), target);
	}

	@Override
	public NickiContext getNamedUserContext(Target target, DynamicObject user, String password)
			throws InvalidPrincipalException {
		return new LdapNamedUserContext(getAdapter(), target, user, password);
	}

	@Override
	public NickiContext getSystemContext(Target target, DynamicObject user) throws InvalidPrincipalException {
		return new LdapSystemContext( getAdapter(), target, user);
	}

	@Override
	public DynamicObjectAdapter getAdapter() {
		return ADAPTER;
	}

}
