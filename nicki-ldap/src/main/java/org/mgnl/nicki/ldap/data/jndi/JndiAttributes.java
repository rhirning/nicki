
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


import javax.naming.directory.Attributes;

import org.mgnl.nicki.core.objects.ContextAttribute;
import org.mgnl.nicki.core.objects.ContextAttributes;


/**
 * The Class JndiAttributes.
 */
public class JndiAttributes implements ContextAttributes {
	
	/** The attributes. */
	Attributes attributes;
	
	/**
	 * Instantiates a new jndi attributes.
	 *
	 * @param attributes the attributes
	 */
	public JndiAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	/**
	 * Gets the.
	 *
	 * @param attributeName the attribute name
	 * @return the context attribute
	 */
	public ContextAttribute get(String attributeName) {
		return new JndiContextAttribute(attributes.get(attributeName));
	}

}
