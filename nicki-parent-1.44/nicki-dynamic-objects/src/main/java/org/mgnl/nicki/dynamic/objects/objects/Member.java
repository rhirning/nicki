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
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

@SuppressWarnings("serial")
public class Member extends DynamicObject implements Serializable{
	public enum RIGHT {NONE, READ, WRITE};

	@Override
	public void initDataModel() {
		addObjectClass("nickiProjectMember");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute(Person.class, "member", "nickiProjectPerson", String.class,
				Config.getProperty("nicki.users.basedn"));
		dynAttribute.setForeignKey(Person.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("directoryRead", "nickiProjectDirectoryRead", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Directory.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("directoryWrite", "nickiProjectDirectoryWrite", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Directory.class);
		addAttribute(dynAttribute);

	}

	@Override
	public String getDisplayName() {
		return getForeignKeyObject(Person.class, "member").getDisplayName();
	}

	@SuppressWarnings("unchecked")
	public List<String> getReadRights() {
		return get("directoryRead") != null?(List<String>) get("directoryRead"): new ArrayList<String>();
	}

	public void setReadRights(List<String> readRights) {
		put("directoryRead", readRights);
	}

	@SuppressWarnings("unchecked")
	public List<String> getWriteRights() {
		return get("directoryWrite") != null?(List<String>) get("directoryWrite"): new ArrayList<String>();
	}

	public void setWriteRights(List<String> writeRights) {
		put("directoryWrite", writeRights);
	}
	
	public void setRight(Directory directory, RIGHT right) {
		if (right == RIGHT.READ) {
			addReadRight(directory);
		} else if (right == RIGHT.WRITE) {
				addWriteRight(directory);
		} else {
			removeRights(directory);
		}
	}
	
	public void setReadRight(Directory directory) {
		addReadRight(directory);
		removeWriteRight(directory);
	}

	public void removeRights(Directory directory) {
		removeReadRight(directory);
		removeWriteRight(directory);
	}

	public void setWriteRight(Directory directory) {
		addWriteRight(directory);
		removeReadRight(directory);
	}
	
	public boolean hasReadRight(Directory directory) {
		return getReadRights().contains(directory.getPath());
	}

	public boolean hasWriteRight(Directory directory) {
		return getWriteRights().contains(directory.getPath());
	}
	
	public void addReadRight(Directory directory) {
		List<String> list = getReadRights();
		if (!list.contains(directory.getPath())) {
			list.add(directory.getPath());
			setReadRights(list);
		}
	}

	public void removeReadRight(Directory directory) {
		List<String> list = getReadRights();
		if (list.contains(directory.getPath())) {
			list.remove(directory.getPath());
			setReadRights(list);
		}
	}

	public void addWriteRight(Directory directory) {
		List<String> list = getWriteRights();
		if (!list.contains(directory.getPath())) {
			list.add(directory.getPath());
			setWriteRights(list);
		}
	}

	public void removeWriteRight(Directory directory) {
		List<String> list = getWriteRights();
		if (list.contains(directory.getPath())) {
			list.remove(directory.getPath());
			setWriteRights(list);
		}
	}
	
	@Override
	public void delete() throws DynamicObjectException {
		setReadRights(null);
		setWriteRights(null);

		update();

		super.delete();
	}


	
}
