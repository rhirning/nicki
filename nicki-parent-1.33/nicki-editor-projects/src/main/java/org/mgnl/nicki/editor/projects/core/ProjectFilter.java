package org.mgnl.nicki.editor.projects.core;

import java.io.Serializable;

import org.mgnl.nicki.dynamic.objects.objects.Project;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;

@SuppressWarnings("serial")
public class ProjectFilter implements EntryFilter, Serializable {
	private DynamicObject user;

	public ProjectFilter(DynamicObject user) {
		super();
		this.user = user;
	}

	public boolean accepts(DynamicObject object) {
		if (object instanceof Project) {
			Project project = (Project) object;
			if (!project.isProjectLeader(user) && !project.isProjectDeputyLeader(user)) {
				return false;
			}
		}
		return true;
	}

}
