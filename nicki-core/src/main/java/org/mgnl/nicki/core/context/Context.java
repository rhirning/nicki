
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

// TODO: Auto-generated Javadoc
/**
 * The Class Context.
 */
public class Context {
	
	/** The request. */
	private Object request;
	
	/** The locale. */
	private Locale locale = Locale.GERMAN;
	
	/** The parameter map. */
	private Map<String, String> parameterMap;

	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public Object getRequest() {
		return request;
	}

	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	public void setRequest(Object request) {
		this.request = request;
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the new locale
	 */
	public void setLocale(Locale locale) {
		if (locale != null) {
			this.locale = locale;
		}
	}

	/**
	 * Sets the request parameters.
	 *
	 * @param map the map
	 */
	public void setRequestParameters(Map<String, String> map) {
		this.parameterMap = map;
	}
	
	/**
	 * Gets the request parameters.
	 *
	 * @return the request parameters
	 */
	public Map<String, String> getRequestParameters() {
		return this.parameterMap;
	}

}
