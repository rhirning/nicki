
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

public class TargetCallbackHandler implements CallbackHandler {

	private String loginTarget;
	private String target;
	private String accessTarget;
	public TargetCallbackHandler() {
	}

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

	public String getLoginTarget() {
		return loginTarget;
	}

	public void setLoginTarget(String loginTarget) {
		this.loginTarget = loginTarget;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getAccessTarget() {
		return accessTarget;
	}

	public void setAccessTarget(String accessTarget) {
		this.accessTarget = accessTarget;
	}
}
