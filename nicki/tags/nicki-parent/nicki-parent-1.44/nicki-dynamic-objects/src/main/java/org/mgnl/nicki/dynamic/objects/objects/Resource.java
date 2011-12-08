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

import java.util.Date;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;



@SuppressWarnings("serial")
public class Resource extends DynamicStructObject {

	@Override
	public void initDataModel() {
		addObjectClass("nrfResource");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute =  new StructuredDynamicAttribute("entitlement", "nrfEntitlementRef", String.class);
		dynAttribute.setForeignKey(Entitlement.class);
		addAttribute(dynAttribute);
		
		dynAttribute =  new StructuredDynamicAttribute("approver", "nrfApprovers", String.class);
		dynAttribute.setForeignKey(Person.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute =  new StructuredDynamicAttribute("revokeApprover", "nrfRevokeApprovers", String.class);
		dynAttribute.setForeignKey(Person.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("localizedName", "nrfLocalizedNames", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("localizedDescription", "nrfLocalizedDescrs", String.class);
		addAttribute(dynAttribute);
}
	
	public DynamicObject getEntitlement() throws DynamicObjectException {
		try {
			return (DynamicObject) execute("getEntitlement", null);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Date getStartTime() {
		return getDateInfo("/assignment/start_tm");
	}
	
	public Date getEndTime() {
		return getDateInfo("/assignment/end_tm");
	}

	public Date getRequestTime() {
		return getDateInfo("/assignment/req_tm");
	}

	public String getInstGUID() {
		return getInfo("/assignment/inst-guid");
	}

	public String getRequester() {
		return getInfo("/assignment/req");
	}
	
	public String getRequestDescription() {
		return getInfo("/assignment/req_desc");
	}

	public String getEntitlementRef() {
		return getInfo("/assignment/ent-ref");
	}

	public String getEntitlementDn() {
		return getInfo("/assignment/ent-dn");
	}

	public String getCauseType() {
		return getInfo("/assignment/cause/type");
	}

	public String getParameter() {
		return getInfo(getInfo("/assignment/ent-ref"), "/ref/param");
	}


}
