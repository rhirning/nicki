package org.mgnl.nicki.core.auth;

import javax.security.auth.callback.Callback;

public class LoginTargetCallback implements Callback {
	private String loginTarget;

	public String getLoginTarget() {
		return loginTarget;
	}

	public void setLoginTarget(String loginTarget) {
		this.loginTarget = loginTarget;
	}

}
