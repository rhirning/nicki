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
