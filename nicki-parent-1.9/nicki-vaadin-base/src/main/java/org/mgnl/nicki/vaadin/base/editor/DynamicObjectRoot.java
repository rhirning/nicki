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

	@Override
	public List<DynamicObject> getChildren(NickiContext context) {
		return context.loadChildObjects(baseDn, "(objectClass=*)");
	}

	@Override
	public DynamicObject getRoot(NickiContext context) {
		return context.loadObject(baseDn);
	}

	@Override
	public String getMessage() {
		return "";
	}

	@Override
	public EntryFilter getEntryFilter() {
		return this.entryFilter;
	}

}
