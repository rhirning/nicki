package org.mgnl.nicki.ldap.objects;

import java.util.Enumeration;

public interface ContextAttribute {

	Enumeration<String> getAll() throws DynamicObjectException;

	Object get() throws DynamicObjectException;

}
