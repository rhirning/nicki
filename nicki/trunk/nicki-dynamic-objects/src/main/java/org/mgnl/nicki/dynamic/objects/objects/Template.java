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
