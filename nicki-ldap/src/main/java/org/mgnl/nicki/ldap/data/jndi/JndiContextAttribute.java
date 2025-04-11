
package org.mgnl.nicki.ldap.data.jndi;

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


import java.util.Enumeration;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.mgnl.nicki.core.objects.ContextAttribute;
import org.mgnl.nicki.core.objects.DynamicObjectException;


// TODO: Auto-generated Javadoc
/**
 * The Class JndiContextAttribute.
 */
public class JndiContextAttribute implements ContextAttribute {

	/** The attribute. */
	Attribute attribute;
	
	/**
	 * Instantiates a new jndi context attribute.
	 *
	 * @param attribute the attribute
	 */
	public JndiContextAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * Gets the all.
	 *
	 * @return the all
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<Object> getAll() throws DynamicObjectException {
		try {
			if (this.attribute != null) {
				return (Enumeration<Object>) this.attribute.getAll();
			}
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
		return new Enumeration<Object>() {
			
			public String nextElement() {
				return null;
			}
			
			public boolean hasMoreElements() {
				return false;
			}
		};
	}

	/**
	 * Gets the.
	 *
	 * @return the object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public Object get() throws DynamicObjectException {
		try {
			return this.attribute.get();
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

}
