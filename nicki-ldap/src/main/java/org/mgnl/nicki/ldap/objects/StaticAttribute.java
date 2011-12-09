/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.objects;

@SuppressWarnings("serial")
public class StaticAttribute extends DynamicAttribute {
	private String value;

	public StaticAttribute(String name, String ldapName, Class<?> attributeClass, String value) {
		super(name, ldapName, attributeClass);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
