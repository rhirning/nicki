
package org.mgnl.nicki.dynamic.objects.objects;

/*-
 * #%L
 * nicki-dynamic-objects
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


import org.mgnl.nicki.core.objects.DynamicObject;


/**
 * The Interface Script.
 */
public interface Script extends DynamicObject {

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	String getData();

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	void setData(String data);

}
