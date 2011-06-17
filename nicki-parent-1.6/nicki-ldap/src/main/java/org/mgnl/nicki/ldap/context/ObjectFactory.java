package org.mgnl.nicki.ldap.context;

import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public interface ObjectFactory {

	DynamicObject createNewDynamicObject(Class<?> classDefinition, String parentPath, String namingValue) throws InstantiateDynamicObjectException;

	DynamicObject getDynamicObject(Class<?> classDefinition, String parentPath,
			String namingValue) throws InstantiateDynamicObjectException;

	DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException;

	DynamicObject getObject(ContextSearchResult contextSearchResult) throws InstantiateDynamicObjectException;

}
