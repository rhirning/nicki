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

import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.Query;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.data.SearchQueryHandler;
import org.mgnl.nicki.core.methods.ReferenceMethod;
import org.mgnl.nicki.core.objects.ChildFilter;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;
import org.mgnl.nicki.core.objects.DynamicObjectException;

public interface NickiContext extends Serializable {
	public static enum READONLY {TRUE, FALSE};
	DynamicObject loadObject(String path);
	<T extends DynamicObject> T loadObject(Class<T> classDefinition, String path);
	
	List<? extends DynamicObject> loadObjects(String baseDn, String filter);
	<T extends DynamicObject> List<T> loadObjects(Class<T> classDefinition, String baseDn, String filter);

	List<? extends DynamicObject> loadChildObjects(String parent,	ChildFilter filter);
	<T extends DynamicObject> T loadChildObject(Class<T> class1, DynamicObject parent, String childKey);
	<T extends DynamicObject> List<T> loadChildObjects(Class<T> classDefinition, String parent,	String filter);
	<T extends DynamicObject> List<T> loadChildObjects(Class<T> class1, DynamicObject parent, String filter);
	
	<T extends DynamicObject> List<T> loadReferenceObjects(Query query);
	<T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, Query query);

	boolean isExist(String path);
	<T extends DynamicObject> T createDynamicObject(Class<T> classDefinition, String parentPath, String namingValue)
			throws  InstantiateDynamicObjectException, DynamicObjectException;
	Target getTarget();
	void updateObject(DynamicObject dynamicObject) throws DynamicObjectException;
	void updateObject(DynamicObject dynamicObject, String[] attributeNames) throws DynamicObjectException;
	DynamicObject createObject(DynamicObject dynamicObject) throws DynamicObjectException;
	void deleteObject(DynamicObject dynamicObject) throws DynamicObjectException;
	DynamicObject moveObject(DynamicObject dynamicObject, String newPath) throws DynamicObjectException;
	DynamicObject renameObject(DynamicObject dynamicObject, String newName) throws DynamicObjectException;
//	void search(QueryHandler queryHandler) throws DynamicObjectException;
	DynamicObjectFactory getObjectFactory();
	NickiPrincipal getPrincipal() throws DynamicObjectException;
	DynamicObject login(String user, String password);
	DynamicObject getUser();
	DynamicObject getPrincipalUser();
	void setUser(DynamicObject user);
	void setPrincipalUser(DynamicObject user);
	String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException;
	void loadObject(DynamicObject dynamicObject) throws DynamicObjectException;
	void loadAttributes(DynamicObject dynamicObject, Class<?> requester, String[] attributes) throws DynamicObjectException;
	void setReadonly(boolean readonly);
	<T extends DynamicObject> T loadObjectAs(Class<T> classDefinition, DynamicObject dynamicObject);
	<T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, ReferenceMethod referenceMethod);
	DynamicObjectAdapter getAdapter();
	void search(QueryHandler handler) throws DynamicObjectException;
	SearchQueryHandler getSearchHandler(Query query);
	Query getQuery(String base);
	<T extends DynamicObject> DataModel getDataModel(Class<T> classDefinition) throws InstantiateDynamicObjectException;
}
