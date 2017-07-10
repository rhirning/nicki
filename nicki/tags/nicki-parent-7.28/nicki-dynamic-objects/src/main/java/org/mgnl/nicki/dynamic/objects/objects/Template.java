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
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;

@SuppressWarnings("serial")
@DynamicObject
@ObjectClass({"nickiTemplate", "organizationalUnit"})
public class Template extends BaseDynamicObject implements EngineTemplate {

	public static final String ATTRIBUTE_DATA = "data";
	public static final String ATTRIBUTE_PARAMS = "params";
	public static final String ATTRIBUTE_HANDLER = "handler";
	public static final String ATTRIBUTE_PARTS = "parts";
	public static final String ATTRIBUTE_FILTER = "filter";
	public static final String ATTRIBUTE_TESTDATA = "testData";
	public static final String ATTRIBUTE_FILE = "file";
	
	@DynamicAttribute(externalName="nickiTemplateParams")
	public String getParams() {
		return getAttribute(ATTRIBUTE_PARAMS);
	}
	
	@DynamicAttribute(externalName="nickiTemplateFile", type=byte[].class)
	public byte[] getFile() {
		return (byte[]) get(ATTRIBUTE_FILE);
	}

	public void setFile(byte[] binary) {
		put(ATTRIBUTE_FILE, binary);
	}
	
	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName="nickiFilter")
	public List<String> getFilter() {
		return (List<String>) get(ATTRIBUTE_FILTER);
	}

	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName="nickiStructuredRef")
	public List<String> getTestData() {
		return (List<String>) get(ATTRIBUTE_TESTDATA);
	}

	@DynamicAttribute(naming=true, externalName="ou")
	public String getName() {
		return super.getName();
	}

	@DynamicAttribute(externalName="nickiTemplateData")
	public String getData() {
		return getAttribute(ATTRIBUTE_DATA);
	}

	public void setData(String data) {
		this.put(ATTRIBUTE_DATA, data);
	}
	
	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName="nickiTemplatePart")
	public List<String> getParts() {
		return (List<String>) get(ATTRIBUTE_PARTS);
	}
	
	public String getPart(String part) {
		List<String> parts = getParts();
		if (parts != null) {
			for (String entry : parts) {
				String key = StringUtils.substringBefore(entry, "=");
				if (StringUtils.equals(key, part)) {
					return StringUtils.substringAfter(entry, "=");
				}
			}
		}
		return null;
	}
	
	public boolean hasPart(String part) {
		return StringUtils.isNotBlank(getPart(part)); 
	}

	@DynamicAttribute(externalName="nickiHandler")
	public String getHandler() {
		return getAttribute(ATTRIBUTE_HANDLER);
	}
	
	public void setHandler(String handler) {
		this.put(ATTRIBUTE_HANDLER, StringUtils.trimToNull(handler));
	}
	
	public boolean hasHandler() {
		return StringUtils.isNotEmpty(getHandler());
	}
	
	public boolean acceptFilter(String filter) {
		if (StringUtils.isEmpty(filter)) {
			return true;
		}

		List<String> filterList = getFilter();
		if (filterList != null && filterList.contains(filter)) {
			return true;
		}
		return false;
	}

}
