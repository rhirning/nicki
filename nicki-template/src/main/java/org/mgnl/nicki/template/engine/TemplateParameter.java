/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.template.engine;

import org.jdom.Element;

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
