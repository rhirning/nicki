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
