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
package org.mgnl.nicki.core.auth;

import java.security.Principal;

import org.mgnl.nicki.core.context.NickiContext;

public class DynamicObjectPrincipal implements Principal {
	
	private NickiPrincipal nickiPrincipal;
	private NickiContext loginContext;
	private NickiContext context;

	public DynamicObjectPrincipal(NickiPrincipal nickiPrincipal, NickiContext loginContext, NickiContext context) {
		super();
		this.nickiPrincipal = nickiPrincipal;
		this.loginContext = loginContext;
		this.context = context;
	}

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

	@Override
	public String getName() {
		return this.nickiPrincipal.getName();
	}

	public NickiContext getLoginContext() {
		return loginContext;
	}

	public NickiContext getContext() {
		return context;
	}
	

}
