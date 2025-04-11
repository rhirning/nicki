
package org.mgnl.nicki.dynamic.objects.reference;

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


import java.util.List;

import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;


// TODO: Auto-generated Javadoc
/**
 * The Class ReferenceDynamicAttribute.
 */
@SuppressWarnings("serial")
public class ReferenceDynamicAttribute extends DynamicAttribute {
	
	/** The base dn. */
	String baseDn;
	
	/** The class ref. */
	Class<? extends DynamicObject> classRef;
	
	/**
	 * Instantiates a new reference dynamic attribute.
	 *
	 * @param classRef the class ref
	 * @param name the name
	 * @param ldapName the ldap name
	 * @param attributeClass the attribute class
	 * @param baseDn the base dn
	 */
	public ReferenceDynamicAttribute(Class<? extends DynamicObject> classRef, String name, String ldapName, Class<?> attributeClass,
			String baseDn) {
		super(name, ldapName, attributeClass);
		this.classRef = classRef;
		this.baseDn = baseDn;
	}

	/**
	 * Gets the options.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the options
	 */
	@Override
	public List<? extends DynamicObject> getOptions(DynamicObject dynamicObject) {
		return dynamicObject.getContext().loadObjects(classRef, baseDn, "");
	}

}
