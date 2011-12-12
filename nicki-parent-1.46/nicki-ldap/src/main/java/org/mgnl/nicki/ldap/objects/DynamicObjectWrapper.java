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

import org.mgnl.nicki.ldap.core.JNDIWrapper;

public class DynamicObjectWrapper extends JNDIWrapper {

	public DynamicObjectWrapper(DynamicObject dynamicObject) {
		super();
		setAttributes(dynamicObject.getModel().getLdapAttributesForCreate(dynamicObject));
	}

}
