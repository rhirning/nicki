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
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicLdapTemplateObject;

@SuppressWarnings("serial")
@DynamicObject(target="edir")
public class Template extends DynamicLdapTemplateObject {

	public static final String ATTRIBUTE_DATA = "data";
	public static final String ATTRIBUTE_PARAMS = "params";
	public static final String ATTRIBUTE_HANDLER = "handler";
	public static final String ATTRIBUTE_PARTS = "parts";
	public static final String ATTRIBUTE_FILTER = "filter";
	public static final String ATTRIBUTE_TESTDATA = "testData";
	public void initDataModel() {
		addObjectClass("nickiTemplate");
		addObjectClass("organizationalUnit");
		DynamicAttribute dynAttribute = new DynamicAttribute(ATTRIBUTE_NAME, "ou", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_DATA, "nickiTemplateData", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute(ATTRIBUTE_PARAMS, "nickiTemplateParams", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute(ATTRIBUTE_HANDLER, "nickiHandler", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute(ATTRIBUTE_PARTS, "nickiTemplatePart", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_FILTER, "nickiFilter", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_TESTDATA, "nickiStructuredRef", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
	};
	
	public String getData() {
		return getAttribute(ATTRIBUTE_DATA);
	}

	public void setData(String data) {
		this.put(ATTRIBUTE_DATA, data);
	}

	@SuppressWarnings("unchecked")
	public List<String> getParts() {
		return (List<String>) get(ATTRIBUTE_PARTS);
	}
	
	public String getHandler() {
		return getAttribute(ATTRIBUTE_HANDLER);
	}
	
	public boolean hasHandler() {
		return StringUtils.isNotEmpty(getHandler());
	}
	
	public boolean acceptFilter(String filter) {
		if (StringUtils.isEmpty(filter)) {
			return true;
		}
		@SuppressWarnings("unchecked")
		List<String> filterList = (List<String>) get(ATTRIBUTE_FILTER);
		if (filterList != null && filterList.contains(filter)) {
			return true;
		}
		return false;
	}

}
