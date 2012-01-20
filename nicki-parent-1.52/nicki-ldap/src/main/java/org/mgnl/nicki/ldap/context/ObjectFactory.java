/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
