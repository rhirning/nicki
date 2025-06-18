
package org.mgnl.nicki.core.auth;


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

/**
 * SSOAdapter.
 */
public interface SSOAdapter {
	
	/**
	 * The Enum TYPE.
	 */
	public enum TYPE {
		
		/** The basic. */
		BASIC,
		
		/** The saml. */
		SAML,
		
		/** The unknown. */
		UNKNOWN
	}
	
	/**
	 * Inits the.
	 *
	 * @param loginModule the login module
	 */
	void init(NickiAdapterLoginModule loginModule);
	
	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	void setRequest(Object request);
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	char[] getPassword();
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	TYPE getType();
}
