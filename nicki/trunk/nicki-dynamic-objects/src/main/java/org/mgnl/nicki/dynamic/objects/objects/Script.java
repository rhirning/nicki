/**
 * This file Copyright (c) 2011 deron Consulting GmbH
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
public class Script extends DynamicTemplateObject {

	public void initDataModel() {
		addObjectClass("nickiScript");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("data", "nickiScriptData", String.class);
		addAttribute(dynAttribute);
	};
	
	public String getData() {
		return getAttribute("data");
	}

	public void setData(String data) {
		this.put("data", data);
	}


}
