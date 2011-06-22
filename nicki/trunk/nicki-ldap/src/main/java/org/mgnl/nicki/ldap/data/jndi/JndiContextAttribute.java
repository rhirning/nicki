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
	@Override
	public Enumeration<Object> getAll() throws DynamicObjectException {
		try {
			if (this.attribute != null) {
				return (Enumeration<Object>) this.attribute.getAll();
			}
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
		return new Enumeration<Object>() {
			
			@Override
			public String nextElement() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean hasMoreElements() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	@Override
	public Object get() throws DynamicObjectException {
		try {
			return this.attribute.get();
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

}
