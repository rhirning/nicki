package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;

@SuppressWarnings("serial")
public class ShowAllFilter implements EntryFilter, Serializable {

	public boolean accepts(DynamicObject object) {
		return true;
	}

}
