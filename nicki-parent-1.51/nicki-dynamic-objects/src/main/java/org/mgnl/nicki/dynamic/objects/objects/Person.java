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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicReference;

@SuppressWarnings("serial")
public class Person extends DynamicTemplateObject {
	public void initDataModel() {
		addObjectClass("Person");
		addAdditionalObjectClass("nickiUserAux");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn",
				String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("surname", "sn", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("givenname", "givenName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("fullname", "fullName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("language", "Language",
				String.class);
		addAttribute(dynAttribute);

		
		dynAttribute = new DynamicReference(Group.class, "memberOf", Config.getProperty("nicki.data.basedn"), 
				"member", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

	}

	@Override
	public String getDisplayName() {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.trimToEmpty(getAttribute("givenname")));
		if (sb.length() > 0) {
			sb.append(" ");
		}
		sb.append(StringUtils.trimToEmpty(getAttribute("surname")));
		sb.append(" (");
		sb.append(getName());
		sb.append(")");
		return sb.toString();
	}


	public String getFullname() {
		return getAttribute("fullname");
	}

	public void setName(String value) {
		put("surname", value);
	}

	public void setGivenName(String value) {
		put("givenname", value);
	}

	public void setLanguage(String value) {
		put("language", value);
	}


}
