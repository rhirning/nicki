/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
