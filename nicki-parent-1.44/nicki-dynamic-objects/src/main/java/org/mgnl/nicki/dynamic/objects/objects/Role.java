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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicReference;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;
import org.mgnl.nicki.ldap.objects.StructuredDynamicReference;


public class Role extends DynamicStructObject {
	
	private static final long serialVersionUID = 6170300879001415636L;

	public void initDataModel() {
		addObjectClass("nrfRole");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicReference(ResourceAssociation.class, "resourceAssociation", Config.getProperty("nicki.system.basedn"), "nrfRole", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute =  new StructuredDynamicAttribute("childRole", "nrfChildRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicReference(Person.class, "member", Config.getProperty("nicki.data.basedn"), "nrfAssignedRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicAttribute("approver", "nrfApprovers", String.class);
		dynAttribute.setForeignKey(Person.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("localizedName", "nrfLocalizedNames", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("localizedDescription", "nrfLocalizedDescrs", String.class);
		addAttribute(dynAttribute);
	};
	
	public Date getStartTime() {
		return getDateInfo("/assignment/start_tm");
	}
	
	public Date getEndTime() {
		try {
			return getDateInfo("/assignment/end_tm");
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getRequester() {
		return getInfo("/assignment/req");
	}
	
	public String getRequestDescription() {
		return getInfo("/assignment/req_desc");
	}

	public Date getUpdateTime() {
		return getDateInfo("/assignment/upd_tm");
	}
	
	public List<Person> getMembers() {
		if (get("member") != null) {
			return getForeignKeyObjects(Person.class, "member");
		} else {
			return new ArrayList<Person>();
		}
	}

}
