
package org.mgnl.nicki.idm.novell.shop.objects;



/*-
 * #%L
 * nicki-idm-novell-shop
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


import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.objects.BaseDynamicObject;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceAssociation.
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("nrfResourceAssociation")
public class ResourceAssociation extends BaseDynamicObject {
	
	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/** The resource. */
	@DynamicReferenceAttribute(externalName="nrfResource", foreignKey=Resource.class, reference=Resource.class,
			baseProperty="nicki.system.basedn")
	private String resource;
	
	/** The param val. */
	@StructuredDynamicAttribute(externalName="nrfDynamicParmVals")
	private String paramVal;
		
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return getAttribute("name");
	}
	/*
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<parameter>
	    <value expr="false" parm-key="EntitlementParamKey">9e3a349cda3f1e489fb82decced0d1ee</value>
	</parameter>
	 */

	/**
	 * Gets the param vals xml.
	 *
	 * @return the param vals xml
	 */
	public String getParamValsXml() {
		return getAttribute("paramVal");
	}
	
	/**
	 * Gets the param value.
	 *
	 * @return the param value
	 */
	public String getParamValue() {
		if (StringUtils.isNotEmpty(getParamValsXml())) {
			return getInfo(getParamValsXml(), "/parameter/value");
		}
		return "";
	}
	
	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	public Resource getResource() {
		if (get("resource") != null) {
			return getForeignKeyObject(Resource.class, "resource");
		} else {
			return null;
		}
	}

}
