package org.mgnl.nicki.dynamic.objects.objects;


import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class Right extends DynamicTemplateObject {
	
	@Override
	public void initDataModel() {
		addObjectClass("nrfRight");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
	}

}
