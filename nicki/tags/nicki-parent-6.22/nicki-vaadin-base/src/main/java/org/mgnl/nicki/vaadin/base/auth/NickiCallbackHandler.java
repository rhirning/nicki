package org.mgnl.nicki.vaadin.base.auth;

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
