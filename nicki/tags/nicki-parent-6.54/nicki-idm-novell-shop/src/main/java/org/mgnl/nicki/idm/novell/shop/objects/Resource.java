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

import java.util.Date;

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;

@DynamicObject
@ObjectClass("nrfResource")
public class Resource extends DynamicStructObject {
	private static final long serialVersionUID = 3550648745922369437L;
	
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@StructuredDynamicAttribute(externalName="nrfEntitlementRef", foreignKey=Entitlement.class)
	private String entitlement;
	@StructuredDynamicAttribute(externalName="nrfApprovers", foreignKey=Person.class)
	private String[] approver;
	@StructuredDynamicAttribute(externalName="nrfRevokeApprovers", foreignKey=Person.class)
	private String[] revokeApprover;
	@DynamicAttribute(externalName="nrfLocalizedNames")
	private String localizedName;
	@DynamicAttribute(externalName="nrfLocalizedDescrs")
	private String localizedDescription;
	
	public Entitlement getEntitlement() throws DynamicObjectException {
		try {
			return (Entitlement) execute("getEntitlement", null);
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
