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
