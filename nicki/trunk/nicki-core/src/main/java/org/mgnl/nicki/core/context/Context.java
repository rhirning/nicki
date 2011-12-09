/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
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
