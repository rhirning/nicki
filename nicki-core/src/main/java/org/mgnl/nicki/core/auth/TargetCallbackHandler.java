
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


import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;


/**
 * CallbackHandler to set targets.
 */
public class TargetCallbackHandler implements CallbackHandler {

	/** The login target. */
	private String loginTarget;
	
	/** The target. */
	private String target;
	
	/** The access target. */
	private String accessTarget;
	
	/**
	 * Instantiates a new target callback handler.
	 */
	public TargetCallbackHandler() {
	}

	/**
	 * Handle.
	 *
	 * @param callbacks the callbacks
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedCallbackException the unsupported callback exception
	 */
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof LoginTargetCallback) {
				LoginTargetCallback callback = (LoginTargetCallback) callbacks[i];
				callback.setLoginTarget(loginTarget);
			} else if (callbacks[i] instanceof TargetCallback) {
				TargetCallback callback = (TargetCallback) callbacks[i];
				callback.setTarget(target);
			} else {
				throw new UnsupportedCallbackException(callbacks[i],
						"Unrecognized Callback");
			}
		}
	}

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

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 *
	 * @param target the new target
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Gets the access target.
	 *
	 * @return the access target
	 */
	public String getAccessTarget() {
		return accessTarget;
	}

	/**
	 * Sets the access target.
	 *
	 * @param accessTarget the new access target
	 */
	public void setAccessTarget(String accessTarget) {
		this.accessTarget = accessTarget;
	}
}
