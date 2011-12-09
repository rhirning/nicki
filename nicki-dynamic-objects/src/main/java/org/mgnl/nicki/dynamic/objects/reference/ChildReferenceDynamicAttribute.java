/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.reference;

import java.util.List;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class ChildReferenceDynamicAttribute extends DynamicAttribute {
	private String filter;

	public ChildReferenceDynamicAttribute(String name, String ldapName, Class<?> attributeClass,
			String filter) {
		super(name, ldapName, attributeClass);
		this.filter = filter;
	}

	@Override
	public List<DynamicObject> getOptions(DynamicObject dynamicObject) {
		return dynamicObject.getContext().loadChildObjects(dynamicObject.getPath(), filter);
	}

}
