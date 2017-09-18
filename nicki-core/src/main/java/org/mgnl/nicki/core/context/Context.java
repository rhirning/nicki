
package org.mgnl.nicki.core.context;

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


import java.util.Locale;
import java.util.Map;

import org.mgnl.nicki.core.objects.DynamicObject;

public class Context {
	private Object request;
	private Locale locale = Locale.GERMAN;
	private DynamicObject user;
	private byte[] credentials;
	private Map<String, String> parameterMap;

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

	public void setRequestParameters(Map<String, String> map) {
		this.parameterMap = map;
	}
	
	public Map<String, String> getRequestParameters() {
		return this.parameterMap;
	}

	public DynamicObject getUser() {
		return user;
	}

	public void setUser(DynamicObject user) {
		this.user = user;
	}

	public byte[] getCredentials() {
		return credentials;
	}

	public void setCredentials(byte[] credentials) {
		this.credentials = credentials;
	}

}
