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

import org.mgnl.nicki.ldap.objects.DynamicAttribute;



public class Group extends DynamicTemplateObject {
	public static final String ATTRIBUTE_DESCRIPTION = "description";
	public static final String ATTRIBUTE_MEMBER = "member";
	public static final String ATTRIBUTE_OWNER = "owner";

	private static final long serialVersionUID = 6170300879001415636L;
	public void initDataModel() {
		addObjectClass("groupOfNames");
		DynamicAttribute dynAttribute = new DynamicAttribute(ATTRIBUTE_NAME, "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_DESCRIPTION, "description", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_MEMBER, "member", String.class);
		dynAttribute.setForeignKey(Person.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_OWNER, "owner", String.class);
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

	};

}
