package org.mgnl.nicki.ldap.data.jndi;

import javax.naming.directory.Attributes;

import org.mgnl.nicki.ldap.objects.ContextAttribute;
import org.mgnl.nicki.ldap.objects.ContextAttributes;

public class JndiAttributes implements ContextAttributes {
	Attributes attributes;
	public JndiAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	@Override
	public ContextAttribute get(String attributeName) {
		return new JndiContextAttribute(attributes.get(attributeName));
	}

}
