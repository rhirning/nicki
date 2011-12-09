/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class DynamicObjectRoot implements DataProvider, Serializable {
	private String baseDn;
	private EntryFilter entryFilter;

	public DynamicObjectRoot(String baseDn, EntryFilter entryFilter) {
		super();
		this.baseDn = baseDn;
		this.entryFilter = entryFilter;
	}

	public List<DynamicObject> getChildren(NickiContext context) {
		return context.loadChildObjects(baseDn, "objectClass=*");
	}

	public DynamicObject getRoot(NickiContext context) {
		return context.loadObject(baseDn);
	}

	public String getMessage() {
		return "";
	}

	public EntryFilter getEntryFilter() {
		return this.entryFilter;
	}

}
