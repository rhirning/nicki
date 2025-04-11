
package org.mgnl.nicki.core.auth;

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


import java.security.Principal;

import org.mgnl.nicki.core.context.NickiContext;

// TODO: Auto-generated Javadoc
/**
 * Principal with aloginContext and accessContext.
 */
public class DynamicObjectPrincipal implements Principal {
	
	/** The nicki principal. */
	private NickiPrincipal nickiPrincipal;
	
	/** The login context. */
	private NickiContext loginContext;
	
	/** The context. */
	private NickiContext context;

	/**
	 * Instantiates a new dynamic object principal.
	 *
	 * @param nickiPrincipal the nicki principal
	 * @param loginContext the login context
	 * @param context the context
	 */
	public DynamicObjectPrincipal(NickiPrincipal nickiPrincipal, NickiContext loginContext, NickiContext context) {
		super();
		this.nickiPrincipal = nickiPrincipal;
		this.loginContext = loginContext;
		this.context = context;
	}

	/**
	 * Instantiates a new dynamic object principal.
	 *
	 * @param name the name
	 * @param loginContext the login context
	 * @param context the context
	 */
	public DynamicObjectPrincipal(String name, NickiContext loginContext, NickiContext context) {
		super();
		try {
			this.nickiPrincipal = new NickiPrincipal(name, "invalid");
		} catch (InvalidPrincipalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.loginContext = loginContext;
		this.context = context;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.nickiPrincipal.getName();
	}

	/**
	 * Gets the login context.
	 *
	 * @return the login context
	 */
	public NickiContext getLoginContext() {
		return loginContext;
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public NickiContext getContext() {
		return context;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("loginUser: ").append(loginContext.getUser());
		sb.append(", user: ").append(context.getUser());
		return sb.toString();
	}
	

}
