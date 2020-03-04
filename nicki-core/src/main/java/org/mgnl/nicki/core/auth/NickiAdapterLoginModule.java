
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


import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.util.Classes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NickiAdapterLoginModule extends NickiLoginModule implements LoginModule {
	
	private SSOAdapter adapter;
	
	@Override
	public boolean login() throws LoginException {
		if (getAdapter() == null) {
			return false;
		}
		log.debug("Using " + getClass().getCanonicalName() +  " with Adapter " + getAdapter().getClass().getCanonicalName());

		NickiPrincipal principal;
		if (StringUtils.isBlank(getAdapter().getName()) || getAdapter().getPassword() == null) {
			log.debug("No valid principal");
			return false;
		}
		
		NickiContext context;
		try {
			principal = new NickiPrincipal(getAdapter().getName(), new String(getAdapter().getPassword()));
			setLoginContext(login(principal));
			context = isUseSystemContext() ? AppContext.getSystemContext(principal.getName(),
					principal.getPassword()): getLoginContext();
		} catch (Exception e) {
			log.debug("Invalid Principal", e);
			return false;
		}

		DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(principal, getLoginContext(), context);
		setPrincipal(dynamicObjectPrincipal);
		setSucceeded(true);
		return true;
	}

	private SSOAdapter getAdapter() {
		if (this.adapter == null) {
			String adapterClass =(String) getOptions().get("adapter");
			try {
				this.adapter = Classes.newInstance(adapterClass);
				this.adapter.init(this);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				log.error("Could not create adapter " + adapterClass, e.getMessage());
			}
		}
		return adapter;
	}
}
