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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.reference.ChildReferenceDynamicAttribute;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class Project extends DynamicObject implements Serializable {
	
	@Override
	public void initDataModel() {
		addObjectClass("nickiProject");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("projectdirectory", "nickiProjectDirectory", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("email", "nickiProjectEmail", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("emailDomain", "nickiProjectEmailDomain", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("emailVisible", "nickiProjectEmailVisible", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("description", "nickiDescription", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute(Person.class, "manager", "nickiOwner", String.class,
				Config.getProperty("nicki.users.basedn"));
		dynAttribute.setMandatory();
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new ChildReferenceDynamicAttribute("deputy", "nickiDeputy", String.class, "objectClass=nickiProjectMember");
		dynAttribute.setForeignKey(Member.class);
		addAttribute(dynAttribute);

		// TODO
		addChild("directory", "objectClass=nickiProjectDirectory");
		addChild("member", "objectClass=nickiProjectMember");
	}

	public String getDescription() {
		String description = getAttribute("description");
		return description!=null?description:"";
	}

	public void setDescription(String description) {
		put("description", description);
	}

	public Person getProjectLeader() {
		return getForeignKeyObject(Person.class, "manager");
	}

	public void setProjectLeader(Person projectLeader) {
		this.put("manager", projectLeader.getPath());
	}

	public boolean isProjectLeader(DynamicObject user) {
		try {
			return StringUtils.equalsIgnoreCase(user.getPath(), getProjectLeader().getPath());
		} catch (Exception e) {
		}
		return false;

	}

	public Member getDeputyProjectLeader() {
		return getForeignKeyObject(Member.class, "deputy");
	}
	
	public boolean isProjectDeputyLeader(DynamicObject user) {
		try {
			return StringUtils.equalsIgnoreCase(user.getPath(),
					getDeputyProjectLeader().getForeignKeyObject(Person.class, "member").getPath());
		} catch (Exception e) {
		}
		return false;

	}

	public void setDeputyProjectLeader(Member deputyProjectLeader) {
		this.put("deputy", deputyProjectLeader.getPath());
	}

	public List<DynamicObject> getMembers() {
		return getChildren("member");
	}

	public List<DynamicObject> getDirectories() {
		return getChildren("directory");
	}

	public void removeMember(Member target) {
		this.getMembers().remove(target);
	}

	public void addDirectory(String name) throws InstantiateDynamicObjectException {
		Directory direcory = getContext().getObjectFactory().getDynamicObject(Directory.class);
		direcory.initNew(getPath(), name);
		this.getDirectories().add(direcory);
	}
}
