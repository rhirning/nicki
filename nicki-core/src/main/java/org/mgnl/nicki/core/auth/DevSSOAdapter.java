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

import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import lombok.extern.slf4j.Slf4j;;
// TODO: Auto-generated Javadoc

/**
 * Adapter which uses the configured credentials (only for local development).
 */
@Slf4j
public class DevSSOAdapter implements SSOAdapter {
	
	/** The login module. */
	private NickiAdapterLoginModule loginModule;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		Target loginTarget = getLoginTarget();
		try {
			String name = AppContext.getSystemContext(loginTarget.getName()).getPrincipal().getName();
			log.debug(loginTarget.getName() + ": name=" + name);
			return name;
		} catch (Exception e) {
			log.error("Error using loginTarget", e);
			return null;
		}
	}

	/**
	 * Gets the login target.
	 *
	 * @return the login target
	 */
	private Target getLoginTarget() {
		Target loginTarget;
		if (loginModule != null) {
			loginTarget = loginModule.getLoginTarget();
		} else {
			loginTarget = TargetFactory.getDefaultTarget();
		}

		return loginTarget;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public char[] getPassword() {
		Target loginTarget = getLoginTarget();
		try {
			return AppContext.getSystemContext(loginTarget.getName()).getPrincipal().getPassword().toCharArray();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public TYPE getType() {
		return TYPE.BASIC;
	}

	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	@Override
	public void setRequest(Object request) {
	}

	/**
	 * Inits the.
	 *
	 * @param loginModule the login module
	 */
	@Override
	public void init(NickiAdapterLoginModule loginModule) {
		this.loginModule = loginModule;
	}

}
