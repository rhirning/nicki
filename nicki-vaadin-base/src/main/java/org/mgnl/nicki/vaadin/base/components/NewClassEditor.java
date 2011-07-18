package org.mgnl.nicki.vaadin.base.components;

import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

import com.vaadin.ui.ComponentContainer;

public interface NewClassEditor extends ComponentContainer{

	void init(DynamicObject parent, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException;

}
