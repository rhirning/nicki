/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
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
