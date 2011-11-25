package org.mgnl.nicki.vaadin.base.editor;

import org.mgnl.nicki.ldap.objects.DynamicObject;

public interface EntryFilter {

	boolean accepts(DynamicObject object);

}
