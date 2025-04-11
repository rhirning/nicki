
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


import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;
import org.mgnl.nicki.core.objects.DynamicObjectException;

// TODO: Auto-generated Javadoc
/**
 * The Class LdapGuestContext.
 */
@SuppressWarnings("serial")
public class LdapGuestContext extends LdapContext implements NickiContext {

	/**
	 * Instantiates a new ldap guest context.
	 *
	 * @param adapter the adapter
	 * @param target the target
	 */
	public LdapGuestContext(DynamicObjectAdapter adapter, Target target) {
		super(adapter, target, READONLY.TRUE);
	}

	/**
	 * Gets the ldap context.
	 *
	 * @return the ldap context
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@Override
	public NickiLdapContext getLdapContext() throws DynamicObjectException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, getTarget().getProperty("providerUrl"));
		// Enable connection pooling
		env.put("com.sun.jndi.ldap.connect.pool",
				DataHelper.booleanOf(getTarget().getProperty("pool")) ? "true" : "false");

		try {
			return new NickiLdapContext(env, null);
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

}
