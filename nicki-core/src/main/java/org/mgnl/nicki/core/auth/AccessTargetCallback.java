package org.mgnl.nicki.core.auth;

import javax.security.auth.callback.Callback;

public class AccessTargetCallback implements Callback {
	private String accessTarget;

	public String getAccessTarget() {
		return accessTarget;
	}

	public void setAccessTarget(String accessTarget) {
		this.accessTarget = accessTarget;
	}


}
