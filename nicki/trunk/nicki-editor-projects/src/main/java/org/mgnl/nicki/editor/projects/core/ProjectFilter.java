package org.mgnl.nicki.editor.projects.core;

import java.io.Serializable;

import org.mgnl.nicki.dynamic.objects.objects.Project;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;

@SuppressWarnings("serial")
public class ProjectFilter implements EntryFilter, Serializable {
	private String user;

	public ProjectFilter(String user) {
		super();
		this.user = user;
	}

	public boolean accepts(DynamicObject object) {
		try {
			Project project = (Project) object;
			if (project.isProjectLeader(user) || project.isProjectDeputyLeader(user)) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

}
