package org.mgnl.nicki.core.auth;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2018 Ralf Hirning
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

import javax.security.auth.callback.Callback;


/**
 * Callback to get the Login target.
 */
public class LoginTargetCallback implements Callback {
	
	/** The login target. */
	private String loginTarget;

	/**
	 * Gets the login target.
	 *
	 * @return the login target
	 */
	public String getLoginTarget() {
		return loginTarget;
	}

	/**
	 * Sets the login target.
	 *
	 * @param loginTarget the new login target
	 */
	public void setLoginTarget(String loginTarget) {
		this.loginTarget = loginTarget;
	}

}
