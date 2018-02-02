package org.mgnl.nicki.vaadin.base.application;

import java.io.Serializable;

import org.mgnl.nicki.core.context.NickiContext;

public class DoubleContext implements Serializable {
	private static final long serialVersionUID = -6658652678574459666L;
		private NickiContext context;
		private NickiContext loginContext;

		public NickiContext getContext() {
			return this.context;
		}

		public void setContext(NickiContext context) {
			this.context = context;
		}

		public NickiContext getLoginContext() {
			return this.loginContext;
		}

		public void setLoginContext(NickiContext loginContext) {
			this.loginContext = loginContext;
		}

}
