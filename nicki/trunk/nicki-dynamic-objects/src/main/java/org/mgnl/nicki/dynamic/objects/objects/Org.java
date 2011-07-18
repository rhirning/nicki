package org.mgnl.nicki.dynamic.objects.objects;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class Org extends DynamicObject implements Serializable {

	@Override
	public void initDataModel() {
		addObjectClass("organizationalUnit");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "ou", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		// TODO
		addChild("child", "objectClass=*");
	}

}
