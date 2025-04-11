
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


import java.util.Enumeration;

// TODO: Auto-generated Javadoc
/**
 * The Interface ContextAttribute.
 */
public interface ContextAttribute {

	/**
	 * Gets the all.
	 *
	 * @return the all
	 * @throws DynamicObjectException the dynamic object exception
	 */
	Enumeration<Object> getAll() throws DynamicObjectException;

	/**
	 * Gets the.
	 *
	 * @return the object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	Object get() throws DynamicObjectException;

}
