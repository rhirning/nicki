package org.mgnl.nicki.dynamic.objects.objects;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class Directory extends DynamicObject implements Serializable {

	@Override
	public void initDataModel() {
		addObjectClass("nickiProjectDirectory");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
	}
}
