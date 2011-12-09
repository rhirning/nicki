/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.objects;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;


@SuppressWarnings("serial")
public class ResourceAssociation extends DynamicTemplateObject {
	
	@Override
	public void initDataModel() {
		addObjectClass("nrfResourceAssociation");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute(Resource.class, "resource", "nrfResource", String.class,
				Config.getProperty("nicki.system.basedn"));
		dynAttribute.setForeignKey(Resource.class);
		addAttribute(dynAttribute);
		
		dynAttribute =  new StructuredDynamicAttribute("paramVal", "nrfDynamicParmVals", String.class);
		addAttribute(dynAttribute);

	}
	
	public String toString() {
		return getAttribute("name");
	}
	/*
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<parameter>
	    <value expr="false" parm-key="EntitlementParamKey">9e3a349cda3f1e489fb82decced0d1ee</value>
	</parameter>
	 */

	public String getParamValsXml() {
		return getAttribute("paramVal");
	}
	
	public String getParamVals() {
		if (StringUtils.isNotEmpty(getParamValsXml())) {
			return getInfo(getParamValsXml(), "/parameter/value");
		}
		return "";
	}

}
