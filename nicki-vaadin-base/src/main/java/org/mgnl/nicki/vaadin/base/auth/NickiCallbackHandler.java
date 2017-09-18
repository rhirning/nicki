
package org.mgnl.nicki.vaadin.base.auth;

/*-
 * #%L
 * nicki-vaadin-base
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

import com.vaadin.ui.AbstractLayout;

public class NickiCallbackHandler implements CallbackHandler {

	private AbstractLayout layout;
	private NickiLoginDialog loginDialog;
	private CredentialsCallback callback;
	
	public NickiCallbackHandler(AbstractLayout layout) {
		super();
		this.layout = layout;
	}

	
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		if (callbacks[0] instanceof CredentialsCallback) {
			callback = (CredentialsCallback) callbacks[0];
			loginDialog = new NickiLoginDialog(this);
			layout.addComponent(loginDialog);
		}
	}


	public void setCredentials(String name, String password) {
		this.callback.setName(name);
		this.callback.setPassword(password);
		layout.removeComponent(loginDialog);
	}

}
