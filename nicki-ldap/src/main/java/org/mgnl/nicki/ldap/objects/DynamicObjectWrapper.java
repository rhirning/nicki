package org.mgnl.nicki.ldap.objects;

import org.mgnl.nicki.ldap.core.JNDIWrapper;

public class DynamicObjectWrapper extends JNDIWrapper {

	public DynamicObjectWrapper(DynamicObject dynamicObject) {
		super();
		setAttributes(dynamicObject.getModel().getLdapAttributesForCreate(dynamicObject));
	}

}
