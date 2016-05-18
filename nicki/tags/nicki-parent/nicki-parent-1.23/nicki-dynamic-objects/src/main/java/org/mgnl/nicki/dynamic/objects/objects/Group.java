package org.mgnl.nicki.dynamic.objects.objects;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;



public class Group extends DynamicTemplateObject {

	private static final long serialVersionUID = 6170300879001415636L;
	public void initDataModel() {
		addObjectClass("nrfGroup");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("description", "description", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("member", "member", String.class);
		dynAttribute.setForeignKey(Person.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("owner", "owner", String.class);
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

	};

}