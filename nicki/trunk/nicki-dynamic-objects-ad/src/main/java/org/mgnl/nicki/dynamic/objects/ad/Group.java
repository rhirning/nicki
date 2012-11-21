/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.ad;

import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.data.OctetString;
import org.mgnl.nicki.ldap.objects.DynamicLdapAttribute;
import org.mgnl.nicki.ldap.objects.StaticAttribute;
import org.mgnl.nicki.ldap.objects.DynamicLdapTemplateObject;

public class Group extends DynamicLdapTemplateObject {
	public enum TYPE {
		SECURITY_GLOBAL (-2147483646),
		SECURITY_LOCAL (-2147483644),
		BUILTIN (-2147483643),
		SECURITY_UNIVERSAL (-2147483640),
		DIST_GLOBAL (2),
		DIST_LOCAL (4),
		DIST_UNIVERSAL (8);

		private final int value;

		TYPE(int val) {
			this.value = val;
		}

		String getValue() {
			return Integer.toString(value);
		}
	};

	private static final long serialVersionUID = 6170300879001415636L;
	public void initDataModel() {
		addObjectClass("group");
		DynamicLdapAttribute dynAttribute = new DynamicLdapAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute("sAMAccountName", "sAMAccountName", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicLdapAttribute("member", "member", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute("guid", "objectGUID", OctetString.class);
		dynAttribute.setReadonly();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute("groupType", "groupType", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute("instanceType", "instanceType", String.class);
		dynAttribute.setReadonly();
		addAttribute(dynAttribute);

		dynAttribute = new StaticAttribute("objectCategory", "objectCategory", String.class,
						Config.getProperty("nicki.target.ad.static.group.objectCategory"));
		dynAttribute.setForeignKey(org.mgnl.nicki.core.objects.DynamicObject.class);
		dynAttribute.setMandatory();
		dynAttribute.setStatic();
		dynAttribute.setReadonly();
		addAttribute(dynAttribute);
	};

	public void setGroupType(TYPE type) {
		put("groupType", type.getValue());
	}

	public void setInstanceType(int type) {
		put("instanceType", Integer.toString(type));
	}

}
