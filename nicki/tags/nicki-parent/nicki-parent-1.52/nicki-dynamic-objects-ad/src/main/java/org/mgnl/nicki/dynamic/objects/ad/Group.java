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
package org.mgnl.nicki.dynamic.objects.ad;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.data.jndi.OctetString;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.StaticAttribute;


public class Group extends DynamicObject {
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
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("sAMAccountName", "sAMAccountName", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("member", "member", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("guid", "objectGUID", OctetString.class);
		dynAttribute.setReadonly();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("groupType", "groupType", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("instanceType", "instanceType", String.class);
		dynAttribute.setReadonly();
		addAttribute(dynAttribute);

		dynAttribute = new StaticAttribute("objectCategory", "objectCategory", String.class,
						Config.getProperty("nicki.target.ad.static.group.objectCategory"));
		dynAttribute.setForeignKey(DynamicObject.class);
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
