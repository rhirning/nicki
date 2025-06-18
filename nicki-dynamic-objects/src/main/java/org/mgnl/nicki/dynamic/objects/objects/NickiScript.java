
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


import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;


/**
 * The Class NickiScript.
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass({ "nickiScript" })
public class NickiScript extends BaseDynamicObject implements Script {

	/** The Constant ATTRIBUTE_DATA. */
	public static final String ATTRIBUTE_DATA = "data";

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@DynamicAttribute(externalName = "cn", naming = true)
	public String getName() {
		return super.getName();
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	@DynamicAttribute(externalName = "nickiScriptData")
	public String getData() {
		return getAttribute(ATTRIBUTE_DATA);
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(String data) {
		this.put(ATTRIBUTE_DATA, data);
	}

}
