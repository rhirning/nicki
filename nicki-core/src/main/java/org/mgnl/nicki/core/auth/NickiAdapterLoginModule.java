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

import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NickiAdapterLoginModule extends NickiLoginModule implements LoginModule {
	private static final Logger LOG = LoggerFactory.getLogger(NickiAdapterLoginModule.class);
	
	private SSOAdapter adapter;
	
	@Override
	public boolean login() throws LoginException {
		LOG.debug("Using " + getClass().getCanonicalName() +  " with Adapter " + getAdapter().getClass().getCanonicalName());

		NickiPrincipal principal;
		try {
			if (StringUtils.isBlank(getAdapter().getName()) || getAdapter().getPassword() == null) {
				LOG.debug("No valid principal");
			}
		} catch (Exception e1) {
			LOG.debug("No valid principal");
		}
		try {
			principal = new NickiPrincipal(getAdapter().getName(), new String(getAdapter().getPassword()));
			setContext(login(principal));
		} catch (Exception e) {
			LOG.debug("Invalid Principal", e);
			return false;
		}

		// TODO: separate context / loginContext
		DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(principal, getContext(), getContext());
		setPrincipal(dynamicObjectPrincipal);
		setSucceeded(true);
		return true;
	}

	private SSOAdapter getAdapter() {
		if (this.adapter == null) {
			String adapterClass =(String) getOptions().get("adapter");
			try {
				this.adapter = Classes.newInstance(adapterClass);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOG.error("Could not create adapter " + adapterClass, e);
			}
		}
		return adapter;
	}
}
