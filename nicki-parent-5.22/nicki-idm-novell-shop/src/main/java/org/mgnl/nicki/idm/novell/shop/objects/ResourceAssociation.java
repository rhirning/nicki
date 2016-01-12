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
package org.mgnl.nicki.idm.novell.shop.objects;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.objects.BaseDynamicObject;

@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("nrfResourceAssociation")
public class ResourceAssociation extends BaseDynamicObject {
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@DynamicReferenceAttribute(externalName="nrfResource", foreignKey=Resource.class, reference=Resource.class,
			baseProperty="nicki.system.basedn")
	private String resource;
	@StructuredDynamicAttribute(externalName="nrfDynamicParmVals")
	private String paramVal;
		
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
