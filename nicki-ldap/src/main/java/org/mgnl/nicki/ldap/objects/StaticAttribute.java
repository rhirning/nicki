
package org.mgnl.nicki.ldap.objects;

/*-
 * #%L
 * nicki-ldap
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


import org.mgnl.nicki.core.objects.DynamicAttribute;


/**
 * The Class StaticAttribute.
 */
@SuppressWarnings("serial")
public class StaticAttribute extends DynamicAttribute {
	
	/** The value. */
	private String value;

	/**
	 * Instantiates a new static attribute.
	 *
	 * @param name the name
	 * @param ldapName the ldap name
	 * @param attributeClass the attribute class
	 * @param value the value
	 */
	public StaticAttribute(String name, String ldapName, Class<?> attributeClass, String value) {
		super(name, ldapName, attributeClass);
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
