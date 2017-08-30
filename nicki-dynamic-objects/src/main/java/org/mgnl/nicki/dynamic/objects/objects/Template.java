/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
