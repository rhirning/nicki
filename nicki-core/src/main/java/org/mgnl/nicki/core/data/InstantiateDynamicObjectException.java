
package org.mgnl.nicki.core.data;


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
 * The Class InstantiateDynamicObjectException.
 */
@SuppressWarnings("serial")
public class InstantiateDynamicObjectException extends Exception {

	/**
	 * Instantiates a new instantiate dynamic object exception.
	 *
	 * @param e the e
	 */
	public InstantiateDynamicObjectException(Exception e) {
		super(e);
	}

	/**
	 * Instantiates a new instantiate dynamic object exception.
	 *
	 * @param text the text
	 */
	public InstantiateDynamicObjectException(String text) {
		super(text);
	}

}
