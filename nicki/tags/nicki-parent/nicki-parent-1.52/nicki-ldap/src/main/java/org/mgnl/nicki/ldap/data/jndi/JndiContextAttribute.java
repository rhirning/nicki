/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.ldap.data.jndi;

import java.util.Enumeration;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.mgnl.nicki.ldap.objects.ContextAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class JndiContextAttribute implements ContextAttribute {

	Attribute attribute;
	public JndiContextAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

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
				// TODO Auto-generated method stub
				return null;
			}
			
			public boolean hasMoreElements() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	public Object get() throws DynamicObjectException {
		try {
			return this.attribute.get();
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

}