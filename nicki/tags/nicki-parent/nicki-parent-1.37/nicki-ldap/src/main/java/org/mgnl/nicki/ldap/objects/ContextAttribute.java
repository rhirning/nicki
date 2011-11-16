package org.mgnl.nicki.ldap.objects;

import java.util.Enumeration;

public interface ContextAttribute {

	Enumeration<Object> getAll() throws DynamicObjectException;

	Object get() throws DynamicObjectException;

}
