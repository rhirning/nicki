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
package org.mgnl.nicki.editor.projects.objects;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.Child;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.dynamic.objects.objects.Person;

@DynamicObject
@ObjectClass({ "nickiProject" })
@Child(name="child", objectFilter={Directory.class, Member.class})
public class Project extends BaseDynamicObject implements Serializable {
	private static final long serialVersionUID = -3250939973069088904L;
	public static final String ATTRIBUTE_PROJECT_DIRECTORY = "projectDirectory";
	public static final String ATTRIBUTE_DESCRIPTION = "description";
	public static final String ATTRIBUTE_OWNER = "owner";
	public static final String ATTRIBUTE_DEPUTY = "deputy";
	public static final String ATTRIBUTE_DIRECTORY = "directory";
	public static final String ATTRIBUTE_MEMBER = "member";


	@DynamicAttribute(externalName = "cn", naming = true)
	private String name;
	@DynamicAttribute(externalName = "nickiProjectDirectory")
	private String projectDirectory;

	@DynamicAttribute(externalName = "nickiDescription")
	private String description;

	@DynamicReferenceAttribute(externalName="nickiOwner", foreignKey=Person.class,
			reference=Person.class,
			baseProperty="nicki.users.basedn")
	private String owner;

	@DynamicReferenceAttribute(externalName="nickiDeputy", foreignKey=Person.class,
	reference=Person.class,
	baseProperty="nicki.users.basedn")
	private String deputy;
	

	public String getDescription() {
		String description = getAttribute(ATTRIBUTE_DESCRIPTION);
		return description!=null?description:"";
	}
	
	public String getProjectDirectory() {
		String directory = getAttribute(ATTRIBUTE_PROJECT_DIRECTORY);
		return directory!=null?directory:"";
	}

	public void setDescription(String description) {
		put(ATTRIBUTE_DESCRIPTION, description);
	}
	
	public void setProjectDirectory(String directory) {
		put(ATTRIBUTE_PROJECT_DIRECTORY, directory);
	}
	
	public void setDirectory(String directory) {
		put(ATTRIBUTE_DIRECTORY, directory);
	}

	public Person getProjectLeader() {
		return getForeignKeyObject(Person.class, ATTRIBUTE_OWNER);
	}

	public void setProjectLeader(Person projectLeader) {
		this.put(ATTRIBUTE_OWNER, projectLeader.getPath());
	}

	public boolean isProjectLeader(org.mgnl.nicki.core.objects.DynamicObject user) {
		try {
			return StringUtils.equalsIgnoreCase(user.getPath(), getProjectLeader().getPath());
		} catch (Exception e) {
		}
		return false;

	}

	public Member getDeputyProjectLeader() {
		return getForeignKeyObject(Member.class, ATTRIBUTE_MEMBER);
	}
	
	public boolean isProjectDeputyLeader(org.mgnl.nicki.core.objects.DynamicObject user) {
		try {
			return StringUtils.equalsIgnoreCase(user.getPath(),
					getDeputyProjectLeader().getForeignKeyObject(Person.class, ATTRIBUTE_MEMBER).getPath());
		} catch (Exception e) {
		}
		return false;

	}

	public void setDeputyProjectLeader(Member deputyProjectLeader) {
		this.put(ATTRIBUTE_DEPUTY, deputyProjectLeader.getPath());
	}

	public List<Member> getMembers() {
		return getChildren(Member.class);
	}

	public List<Directory> getDirectories() {
		return getChildren(Directory.class);
	}

	public void removeMember(Member target) {
		this.getMembers().remove(target);
	}

	public void addDirectory(String name) throws InstantiateDynamicObjectException {
		Directory direcory = getContext().getObjectFactory().getNewDynamicObject(Directory.class,
				getPath(), name);
		this.getDirectories().add(direcory);
	}
}
