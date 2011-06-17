package org.mgnl.nicki.ldap.auth;

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