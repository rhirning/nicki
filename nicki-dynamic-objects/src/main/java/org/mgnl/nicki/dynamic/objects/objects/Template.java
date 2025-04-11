
package org.mgnl.nicki.dynamic.objects.objects;

/*-
 * #%L
 * nicki-dynamic-objects
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
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
 * #L%
 */


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;

// TODO: Auto-generated Javadoc
/**
 * The Class Template.
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass({"nickiTemplate", "organizationalUnit"})
public class Template extends BaseDynamicObject implements EngineTemplate {

	/** The Constant ATTRIBUTE_DATA. */
	public static final String ATTRIBUTE_DATA = "data";
	
	/** The Constant ATTRIBUTE_PARAMS. */
	public static final String ATTRIBUTE_PARAMS = "params";
	
	/** The Constant ATTRIBUTE_HANDLER. */
	public static final String ATTRIBUTE_HANDLER = "handler";
	
	/** The Constant ATTRIBUTE_PARTS. */
	public static final String ATTRIBUTE_PARTS = "parts";
	
	/** The Constant ATTRIBUTE_FILTER. */
	public static final String ATTRIBUTE_FILTER = "filter";
	
	/** The Constant ATTRIBUTE_TESTDATA. */
	public static final String ATTRIBUTE_TESTDATA = "testData";
	
	/** The Constant ATTRIBUTE_FILE. */
	public static final String ATTRIBUTE_FILE = "file";
	
	/**
	 * Gets the params.
	 *
	 * @return the params
	 */
	@DynamicAttribute(externalName="nickiTemplateParams")
	public String getParams() {
		return getAttribute(ATTRIBUTE_PARAMS);
	}
	
	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	@DynamicAttribute(externalName="nickiTemplateFile", type=byte[].class)
	public byte[] getFile() {
		return (byte[]) get(ATTRIBUTE_FILE);
	}

	/**
	 * Sets the file.
	 *
	 * @param binary the new file
	 */
	public void setFile(byte[] binary) {
		put(ATTRIBUTE_FILE, binary);
	}
	
	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName="nickiFilter")
	public List<String> getFilter() {
		return (List<String>) get(ATTRIBUTE_FILTER);
	}

	/**
	 * Gets the test data.
	 *
	 * @return the test data
	 */
	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName="nickiStructuredRef")
	public List<String> getTestData() {
		return (List<String>) get(ATTRIBUTE_TESTDATA);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@DynamicAttribute(naming=true, externalName="ou")
	public String getName() {
		return super.getName();
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	@DynamicAttribute(externalName="nickiTemplateData")
	public String getData() {
		return getAttribute(ATTRIBUTE_DATA);
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(String data) {
		this.put(ATTRIBUTE_DATA, data);
	}
	
	/**
	 * Gets the parts.
	 *
	 * @return the parts
	 */
	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName="nickiTemplatePart")
	public List<String> getParts() {
		return (List<String>) get(ATTRIBUTE_PARTS);
	}
	
	/**
	 * Gets the part.
	 *
	 * @param part the part
	 * @return the part
	 */
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
	
	/**
	 * Checks for part.
	 *
	 * @param part the part
	 * @return true, if successful
	 */
	public boolean hasPart(String part) {
		return StringUtils.isNotBlank(getPart(part)); 
	}

	/**
	 * Gets the handler.
	 *
	 * @return the handler
	 */
	@DynamicAttribute(externalName="nickiHandler")
	public String getHandler() {
		return getAttribute(ATTRIBUTE_HANDLER);
	}
	
	/**
	 * Sets the handler.
	 *
	 * @param handler the new handler
	 */
	public void setHandler(String handler) {
		this.put(ATTRIBUTE_HANDLER, StringUtils.trimToNull(handler));
	}
	
	/**
	 * Checks for handler.
	 *
	 * @return true, if successful
	 */
	public boolean hasHandler() {
		return StringUtils.isNotEmpty(getHandler());
	}
	
	/**
	 * Accept filter.
	 *
	 * @param filter the filter
	 * @return true, if successful
	 */
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
