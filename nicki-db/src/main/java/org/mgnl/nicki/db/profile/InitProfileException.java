
package org.mgnl.nicki.db.profile;

/*-
 * #%L
 * nicki-db
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


import javax.naming.NamingException;


/**
 * The Class InitProfileException.
 */
@SuppressWarnings("serial")
public class InitProfileException extends Exception {

	/**
	 * Instantiates a new inits the profile exception.
	 *
	 * @param e the e
	 */
	public InitProfileException(NamingException e) {
		super(e);
	}

}
