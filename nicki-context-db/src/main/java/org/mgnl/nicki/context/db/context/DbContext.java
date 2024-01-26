
package org.mgnl.nicki.context.db.context;

/*-
 * #%L
 * nicki-context-db
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

import javax.naming.NamingException;

import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.context.BeanQueryHandler;
import org.mgnl.nicki.core.context.DynamicObjectFactory;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
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

public class DbContext implements NickiContext {
	private static final long serialVersionUID = 1516385017383833100L;

	@Override
	public DynamicObject loadObject(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> T loadObject(Class<T> classDefinition, String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends DynamicObject> loadObjects(String baseDn, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> List<T> loadObjects(Class<T> classDefinition, String baseDn, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends DynamicObject> loadChildObjects(String parent, ChildFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> T loadChildObject(Class<T> class1, DynamicObject parent, String childKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> classDefinition, String parent, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> class1, DynamicObject parent, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> List<T> loadReferenceObjects(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExist(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DynamicObject> T createDynamicObject(Class<T> classDefinition, String parentPath,
			String namingValue) throws InstantiateDynamicObjectException, DynamicObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Target getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateObject(DynamicObject dynamicObject) throws DynamicObjectException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(DynamicObject dynamicObject, String[] attributeNames) throws DynamicObjectException {
		// TODO Auto-generated method stub

	}

	@Override
	public DynamicObject createObject(DynamicObject dynamicObject) throws DynamicObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(DynamicObject dynamicObject) throws DynamicObjectException {
		// TODO Auto-generated method stub

	}

	@Override
	public DynamicObject moveObject(DynamicObject dynamicObject, String newPath) throws DynamicObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DynamicObject renameObject(DynamicObject dynamicObject, String newName) throws DynamicObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DynamicObjectFactory getObjectFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NickiPrincipal getPrincipal() throws DynamicObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DynamicObject login(String user, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DynamicObject getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DynamicObject getPrincipalUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUser(DynamicObject user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPrincipalUser(DynamicObject user) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition)
			throws InstantiateDynamicObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadObject(DynamicObject dynamicObject) throws DynamicObjectException {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadAttributes(DynamicObject dynamicObject, Class<?> requester, String[] attributes)
			throws DynamicObjectException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReadonly(boolean readonly) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DynamicObject> T loadObjectAs(Class<T> classDefinition, DynamicObject dynamicObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition,
			ReferenceMethod referenceMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DynamicObjectAdapter getAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void search(QueryHandler handler) throws DynamicObjectException {
		// TODO Auto-generated method stub

	}

	@Override
	public SearchQueryHandler getSearchHandler(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query getQuery(String base) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> DataModel getDataModel(Class<T> classDefinition)
			throws InstantiateDynamicObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void search(BeanQueryHandler queryHandler) throws NamingException {
		// TODO Auto-generated method stub
		
	}

}
