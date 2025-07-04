
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


import java.io.Serializable;

import org.mgnl.nicki.core.annotation.Child;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;


/**
 * The Class Org.
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("organizationalUnit")
@Child(name="child")
public class Org extends BaseDynamicObject implements Serializable {
	
	/** The Constant ATTRIBUTE_CHILD. */
	public static final String ATTRIBUTE_CHILD = "child";
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@DynamicAttribute(externalName="ou", naming=true)
	public String getName() {
		return super.getName();
	}
		
}
