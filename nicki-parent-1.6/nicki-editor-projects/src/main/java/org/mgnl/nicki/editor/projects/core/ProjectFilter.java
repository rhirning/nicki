package org.mgnl.nicki.editor.projects.core;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;

@SuppressWarnings("serial")
public class ProjectFilter implements EntryFilter, Serializable {

	public boolean accepts(DynamicObject object) {
		return true;
	}

}
