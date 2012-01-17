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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class Template extends DynamicTemplateObject {

	public void initDataModel() {
		addObjectClass("nickiTemplate");
		addObjectClass("organizationalUnit");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "ou", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("data", "nickiTemplateData", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("params", "nickiTemplateParams", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("handler", "nickiHandler", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("parts", "nickiTemplatePart", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("filter", "nickiFilter", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("testData", "nickiStructuredRef", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
	};
	
	public String getData() {
		return getAttribute("data");
	}

	public void setData(String data) {
		this.put("data", data);
	}

	@SuppressWarnings("unchecked")
	public List<String> getParts() {
		return (List<String>) get("parts");
	}
	
	public String getHandler() {
		return getAttribute("handler");
	}
	
	public boolean hasHandler() {
		return StringUtils.isNotEmpty(getHandler());
	}
	
	public boolean acceptFilter(String filter) {
		if (StringUtils.isEmpty(filter)) {
			return true;
		}
		@SuppressWarnings("unchecked")
		List<String> filterList = (List<String>) get("filter");
		if (filterList != null && filterList.contains(filter)) {
			return true;
		}
		return false;
	}

}
