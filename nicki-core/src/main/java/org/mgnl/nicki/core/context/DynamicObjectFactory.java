/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.core.context;

import java.util.List;

import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;

public interface DynamicObjectFactory {

	<T extends DynamicObject> T createNewDynamicObject(Class<T> classDefinition, String parentPath, String namingValue) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> T getNewDynamicObject(Class<T> classDefinition, String parentPath,
			String namingValue) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> T getDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException;
	
	DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException;

	String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException;

	<T extends TreeData> List<T> findDynamicObjects(Class<T> classDefinition) throws InstantiateDynamicObjectException;

	DynamicObject getObject(ContextSearchResult contextSearchResult) throws InstantiateDynamicObjectException;

	<T extends DynamicObject> T getObject(ContextSearchResult rs, Class<T> classDefinition)
			throws InstantiateDynamicObjectException, DynamicObjectException;

}
