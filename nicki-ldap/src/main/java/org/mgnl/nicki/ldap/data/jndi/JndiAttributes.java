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

import javax.naming.directory.Attributes;

import org.mgnl.nicki.ldap.objects.ContextAttribute;
import org.mgnl.nicki.ldap.objects.ContextAttributes;

public class JndiAttributes implements ContextAttributes {
	Attributes attributes;
	public JndiAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public ContextAttribute get(String attributeName) {
		return new JndiContextAttribute(attributes.get(attributeName));
	}

}
