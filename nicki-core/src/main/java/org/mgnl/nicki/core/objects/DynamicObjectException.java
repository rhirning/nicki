
package org.mgnl.nicki.core.objects;


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
 * The Class DynamicObjectException.
 */
@SuppressWarnings("serial")
public class DynamicObjectException extends Exception {

	/**
	 * Instantiates a new dynamic object exception.
	 *
	 * @param message the message
	 */
	public DynamicObjectException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new dynamic object exception.
	 *
	 * @param e the e
	 */
	public DynamicObjectException(Exception e) {
		super(e);
	}

}
