/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.editor.projects.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;

@DynamicObject
@ObjectClass({ "nickiProjectMember" })
@SuppressWarnings("serial")
public class Member extends BaseDynamicObject implements Serializable {

	public enum RIGHT {

		NONE, READ, WRITE
	};

	public static final String ATTRIBUTE_DIRECTORY_READ = "directoryRead";
	public static final String ATTRIBUTE_DIRECTORY_WRITE = "directoryWrite";
	public static final String ATTRIBUTE_MEMBER = "member";
	

	@DynamicAttribute(externalName = "cn", naming = true)
	private String name;
	@DynamicAttribute(externalName = "nickiProjectPerson")
	private String member;
	@DynamicAttribute(externalName = "nickiProjectDirectoryRead")
	private String directoryRead[];
	@DynamicAttribute(externalName = "nickiProjectDirectoryWrite")
	private String directoryWrite[];
	
	@Override
	public String getDisplayName() {
		return getForeignKeyObject(Person.class, ATTRIBUTE_MEMBER).getDisplayName();
	}

	public Person getPerson() {
		return getForeignKeyObject(Person.class, ATTRIBUTE_MEMBER);
	}

	@SuppressWarnings("unchecked")
	public List<String> getReadRights() {
		return get(ATTRIBUTE_DIRECTORY_READ) != null ? (List<String>) get(ATTRIBUTE_DIRECTORY_READ) : new ArrayList<String>();
	}

	public void setReadRights(List<String> readRights) {
		put(ATTRIBUTE_DIRECTORY_READ, readRights);
	}

	@SuppressWarnings("unchecked")
	public List<String> getWriteRights() {
		return get(ATTRIBUTE_DIRECTORY_WRITE) != null ? (List<String>) get(ATTRIBUTE_DIRECTORY_WRITE) : new ArrayList<String>();
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
