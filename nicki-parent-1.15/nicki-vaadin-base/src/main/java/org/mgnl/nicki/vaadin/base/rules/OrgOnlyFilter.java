package org.mgnl.nicki.vaadin.base.rules;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;

public class OrgOnlyFilter implements EntryFilter {

	@Override
	public boolean accepts(DynamicObject object) {
		if (object.getModel().getObjectClasses().contains("organizationalUnit")) {
			return true;
		}
		return false;
	}

}
