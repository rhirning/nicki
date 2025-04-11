
package org.mgnl.nicki.template.engine;

/*-
 * #%L
 * nicki-template
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


import org.jdom2.Element;

// TODO: Auto-generated Javadoc
/**
 * The Class TemplateParameter.
 */
public class TemplateParameter {
	
	/** The name. */
	private String name;
	
	/** The display name. */
	private String displayName;
	
	/** The data type. */
	private String dataType;
	
	/** The value. */
	private String value;
	
	/**
	 * Instantiates a new template parameter.
	 *
	 * @param attributeElement the attribute element
	 */
	public TemplateParameter(Element attributeElement) {
		this.name = attributeElement.getAttributeValue("name");
		this.displayName = attributeElement.getAttributeValue("displayName");
		this.dataType = attributeElement.getAttributeValue("dataType");
		this.value = attributeElement.getAttributeValue("value");
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
