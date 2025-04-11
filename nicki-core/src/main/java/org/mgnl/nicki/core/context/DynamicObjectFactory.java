
package org.mgnl.nicki.core.context;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
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
 * #L%
 */


import java.util.List;

import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating DynamicObject objects.
 */
public interface DynamicObjectFactory {

	/**
	 * Creates a new DynamicObject object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param parentPath the parent path
	 * @param namingValue the naming value
	 * @return the t
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	<T extends DynamicObject> T createNewDynamicObject(Class<T> classDefinition, String parentPath, String namingValue) throws InstantiateDynamicObjectException;

	/**
	 * Gets the new dynamic object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param parentPath the parent path
	 * @param namingValue the naming value
	 * @return the new dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	<T extends DynamicObject> T getNewDynamicObject(Class<T> classDefinition, String parentPath,
			String namingValue) throws InstantiateDynamicObjectException;

	/**
	 * Gets the dynamic object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	<T extends DynamicObject> T getDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException;
	
	/**
	 * Gets the dynamic object.
	 *
	 * @param className the class name
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException;

	/**
	 * Gets the object class filter.
	 *
	 * @param nickiContext the nicki context
	 * @param classDefinition the class definition
	 * @return the object class filter
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException;

	/**
	 * Find dynamic objects.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the list
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	<T extends TreeData> List<T> findDynamicObjects(Class<T> classDefinition) throws InstantiateDynamicObjectException;

	/**
	 * Gets the object.
	 *
	 * @param contextSearchResult the context search result
	 * @return the object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	DynamicObject getObject(ContextSearchResult contextSearchResult) throws InstantiateDynamicObjectException;

	/**
	 * Gets the object.
	 *
	 * @param <T> the generic type
	 * @param rs the rs
	 * @param classDefinition the class definition
	 * @return the object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 * @throws DynamicObjectException the dynamic object exception
	 */
	<T extends DynamicObject> T getObject(ContextSearchResult rs, Class<T> classDefinition)
			throws InstantiateDynamicObjectException, DynamicObjectException;

}
