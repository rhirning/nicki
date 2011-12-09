/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.ad;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class Person extends DynamicObject {

	public void initDataModel()
	{
		addObjectClass("person");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("surname", "sn", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("givenname", "givenName", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("fullname", "displayName", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("group", "memberOf", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Group.class);
		addAttribute(dynAttribute);

	}
	
	
	public String getFullname() {
		return getAttribute("fullname");
	}
}
