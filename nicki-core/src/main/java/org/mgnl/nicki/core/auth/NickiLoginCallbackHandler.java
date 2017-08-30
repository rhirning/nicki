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

import javax.security.auth.callback.CallbackHandler;

public abstract class NickiLoginCallbackHandler implements CallbackHandler {
	private Object request;
	private SSOAdapter adapter;

	public NickiLoginCallbackHandler(Object request) {
		this.request = request;
	}

	protected Object getRequest() {
		return request;
	}

	public void setAdapter(SSOAdapter ssoAdapter) {
		this.adapter = ssoAdapter;
	}

	protected SSOAdapter getAdapter() {
		return adapter;
	}
}