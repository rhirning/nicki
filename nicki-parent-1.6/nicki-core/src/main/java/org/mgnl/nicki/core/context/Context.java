package org.mgnl.nicki.core.context;

import java.util.Locale;

public class Context {
	private Object request = null;
	private Object response = null;
	private Locale locale = Locale.GERMAN;

	public Object getRequest() {
		return request;
	}

	public void setRequest(Object request) {
		this.request = request;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		if (locale != null) {
			this.locale = locale;
		}
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public Object getResponse() {
		return response;
	}

}
