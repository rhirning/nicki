/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.context;

import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public interface ObjectFactory {

	<T extends DynamicObject> T createNewDynamicObject(Class<T> classDefinition, String parentPath, String namingValue) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> T getNewDynamicObject(Class<T> classDefinition, String parentPath,
			String namingValue) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> T getDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException;
	
	DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException;

	DynamicObject getObject(ContextSearchResult contextSearchResult) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> T getObject(ContextSearchResult contextSearchResult, Class<T> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException;

	String getObjectClassFilter(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException;

	String getNamingLdapAttribute(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException;

}
