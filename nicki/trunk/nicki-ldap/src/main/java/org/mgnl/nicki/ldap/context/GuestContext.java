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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.mgnl.nicki.ldap.objects.DynamicObjectException;

@SuppressWarnings("serial")
public class GuestContext extends BasicContext implements NickiContext {

	protected GuestContext(Target target, READONLY readonly) {
		super(target, readonly);
	}

	public DirContext getDirContext() throws DynamicObjectException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, getTarget().getProperty("providerUrl"));
		// Enable connection pooling
		env.put("com.sun.jndi.ldap.connect.pool", "true");

		try {
			return new InitialDirContext(env);
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

}
