
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

public class TemplateParameter {
	private String name;
	private String displayName;
	private String dataType;
	private String value;
	
	public TemplateParameter(Element attributeElement) {
		this.name = attributeElement.getAttributeValue("name");
		this.displayName = attributeElement.getAttributeValue("displayName");
		this.dataType = attributeElement.getAttributeValue("dataType");
		this.value = attributeElement.getAttributeValue("value");
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDataType() {
		return dataType;
	}

	public String getValue() {
		return value;
	}
}
