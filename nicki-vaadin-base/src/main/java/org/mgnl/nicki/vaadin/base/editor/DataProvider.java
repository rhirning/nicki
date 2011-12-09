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

import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public interface DataProvider {

	public List<DynamicObject> getChildren(NickiContext context);
	public DynamicObject getRoot(NickiContext context);
	public String getMessage();
	public EntryFilter getEntryFilter();

}
