/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.objects;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;


@SuppressWarnings("serial")
public class Entitlement extends DynamicStructObject {
	
	@Override
	public void initDataModel() {
		addObjectClass("DirXML-Entitlement");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

	}
	
	public String getSource() {
		return getInfo("/ref/src");
	}
	
	public String getId() {
		return getInfo("/ref/id");
	}
	
	public String getParameter() {
		return getInfo("/ref/param");
	}


}
