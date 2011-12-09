/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
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
