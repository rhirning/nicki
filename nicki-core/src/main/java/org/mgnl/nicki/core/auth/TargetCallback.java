package org.mgnl.nicki.core.auth;

import javax.security.auth.callback.Callback;

public class TargetCallback implements Callback {
	private String target;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
