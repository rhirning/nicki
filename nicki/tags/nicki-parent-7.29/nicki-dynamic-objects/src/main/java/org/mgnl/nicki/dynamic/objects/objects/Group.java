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
import java.util.Collection;
import java.util.List;

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;

@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("groupOfNames")
public class Group extends BaseDynamicObject {
	public static final String ATTRIBUTE_DESCRIPTION = "description";
	public static final String ATTRIBUTE_MEMBER = "member";
	public static final String ATTRIBUTE_OWNER = "owner";

	@DynamicAttribute(externalName="cn", naming=true)
	public String getName() {
		return super.getName();
	}
	
	@DynamicAttribute(externalName="description")
	public String getDescription() {
		return getAttribute(ATTRIBUTE_DESCRIPTION);
	}
	
	public void setDescription(String description) {
		put(ATTRIBUTE_DESCRIPTION, description);
	}
	
	@DynamicAttribute(externalName="owner", foreignKey=Person.class)
	public String getOwner() {
		return getAttribute(ATTRIBUTE_OWNER);
	}
		
    @SuppressWarnings("unchecked")
	@DynamicAttribute(externalName = "member", foreignKey=Person.class)
    public List<String> getMember() {
    	return (List<String>) get(ATTRIBUTE_MEMBER);
    }
    

	public Collection<Person> getMembers() {
		return getForeignKeyObjects(Person.class, "member");
	}

	public void addMember(String path) {
		List<String> list = getMember();
		if (list == null) {
			list = new ArrayList<>();
		}
		if (!list.contains(path)) {
			list.add(path);
		}
	}
	
	public void removeMember(String path) {
		List<String> list = getMember();
		if (list != null && list.contains(path)) {
			list.remove(path);
		}
	}
}
