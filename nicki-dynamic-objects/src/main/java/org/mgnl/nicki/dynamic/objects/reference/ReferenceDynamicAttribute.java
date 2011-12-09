/**
 * This file Copyright (c) 2011 deron Consulting GmbH
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
public class ReferenceDynamicAttribute extends DynamicAttribute {
	String baseDn;
	Class<? extends DynamicObject> classRef;
	public ReferenceDynamicAttribute(Class<? extends DynamicObject> classRef, String name, String ldapName, Class<?> attributeClass,
			String baseDn) {
		super(name, ldapName, attributeClass);
		this.classRef = classRef;
		this.baseDn = baseDn;
	}

	@Override
	public List<? extends DynamicObject> getOptions(DynamicObject dynamicObject) {
		return dynamicObject.getContext().loadObjects(classRef, baseDn, "");
	}

}
