
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


import javax.security.auth.callback.CallbackHandler;
// TODO: Auto-generated Javadoc

/**
 * CallbackHandler with request object and ssoAdapter.
 */
public abstract class NickiLoginCallbackHandler implements CallbackHandler {
	
	/** The request. */
	private Object request;
	
	/** The adapter. */
	private SSOAdapter adapter;

	/**
	 * Instantiates a new nicki login callback handler.
	 *
	 * @param request the request
	 */
	public NickiLoginCallbackHandler(Object request) {
		this.request = request;
	}

	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	protected Object getRequest() {
		return request;
	}

	/**
	 * Sets the adapter.
	 *
	 * @param ssoAdapter the new adapter
	 */
	public void setAdapter(SSOAdapter ssoAdapter) {
		this.adapter = ssoAdapter;
	}

	/**
	 * Gets the adapter.
	 *
	 * @return the adapter
	 */
	protected SSOAdapter getAdapter() {
		return adapter;
	}
}
