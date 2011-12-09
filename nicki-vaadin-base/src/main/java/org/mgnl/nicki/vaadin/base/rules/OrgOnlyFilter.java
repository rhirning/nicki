/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.rules;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;

public class OrgOnlyFilter implements EntryFilter {

	public boolean accepts(DynamicObject object) {
		if (object.getModel().getObjectClasses().contains("organizationalUnit")) {
			return true;
		}
		return false;
	}

}
