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
package org.mgnl.nicki.editor.projects.core;

import java.io.Serializable;

import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.data.EntryFilter;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.editor.projects.objects.Project;

@SuppressWarnings("serial")
public class ProjectFilter implements EntryFilter, Serializable {
	private DynamicObject user;

	public ProjectFilter(DynamicObject user) {
		super();
		this.user = user;
	}

	public boolean accepts(TreeData object) {
		if (object instanceof Project) {
			Project project = (Project) object;
			if (!project.isProjectLeader(user) && !project.isProjectDeputyLeader(user)) {
				return false;
			}
		}
		return true;
	}

}
