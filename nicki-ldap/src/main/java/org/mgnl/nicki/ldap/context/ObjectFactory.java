package org.mgnl.nicki.ldap.context;

import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public interface ObjectFactory {

	<T extends DynamicObject> T createNewDynamicObject(Class<T> classDefinition, String parentPath, String namingValue) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> T getDynamicObject(Class<T> classDefinition, String parentPath,
			String namingValue) throws InstantiateDynamicObjectException;

	DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException;

	DynamicObject getObject(ContextSearchResult contextSearchResult) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> T getObject(ContextSearchResult contextSearchResult, Class<T> classDefinition) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> String getObjectClassFilter(Class<T> classDefinition) throws InstantiateDynamicObjectException;

}
