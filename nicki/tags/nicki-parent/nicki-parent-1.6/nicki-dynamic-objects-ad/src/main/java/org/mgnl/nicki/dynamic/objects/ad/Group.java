package org.mgnl.nicki.dynamic.objects.ad;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;



public class Group extends DynamicObject {

	private static final long serialVersionUID = 6170300879001415636L;
	public void initDataModel() {
		addObjectClass("group");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("member", "member", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

	};

}