package org.mgnl.nicki.ldap.context;

/*-
 * #%L
 * nicki-ldap
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;

public class NickiLdapContext extends InitialLdapContext implements AutoCloseable {

	public NickiLdapContext() throws NamingException {
		super();
	}

	public NickiLdapContext(Hashtable<String, String> env, Control[] connCtls) throws NamingException {
		super(env,connCtls);
	}

}
