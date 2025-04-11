
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


import java.io.Serializable;
import java.util.List;

import javax.naming.NamingException;

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

// TODO: Auto-generated Javadoc
/**
 * The Interface NickiContext.
 */
public interface NickiContext extends Serializable {
	
	/**
	 * The Enum READONLY.
	 */
	public static enum READONLY {
/** The true. */
TRUE, 
 /** The false. */
 FALSE};
	
	/**
	 * Load object.
	 *
	 * @param path the path
	 * @return the dynamic object
	 */
	DynamicObject loadObject(String path);
	
	/**
	 * Load object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param path the path
	 * @return the t
	 */
	<T extends DynamicObject> T loadObject(Class<T> classDefinition, String path);
	
	/**
	 * Load objects.
	 *
	 * @param baseDn the base dn
	 * @param filter the filter
	 * @return the list<? extends dynamic object>
	 */
	List<? extends DynamicObject> loadObjects(String baseDn, String filter);
	
	/**
	 * Load objects.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param baseDn the base dn
	 * @param filter the filter
	 * @return the list
	 */
	<T extends DynamicObject> List<T> loadObjects(Class<T> classDefinition, String baseDn, String filter);

	/**
	 * Load child objects.
	 *
	 * @param parent the parent
	 * @param filter the filter
	 * @return the list<? extends dynamic object>
	 */
	List<? extends DynamicObject> loadChildObjects(String parent,	ChildFilter filter);
	
	/**
	 * Load child object.
	 *
	 * @param <T> the generic type
	 * @param class1 the class 1
	 * @param parent the parent
	 * @param childKey the child key
	 * @return the t
	 */
	<T extends DynamicObject> T loadChildObject(Class<T> class1, DynamicObject parent, String childKey);
	
	/**
	 * Load child objects.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param parent the parent
	 * @param filter the filter
	 * @return the list
	 */
	<T extends DynamicObject> List<T> loadChildObjects(Class<T> classDefinition, String parent,	String filter);
	
	/**
	 * Load child objects.
	 *
	 * @param <T> the generic type
	 * @param class1 the class 1
	 * @param parent the parent
	 * @param filter the filter
	 * @return the list
	 */
	<T extends DynamicObject> List<T> loadChildObjects(Class<T> class1, DynamicObject parent, String filter);
	
	/**
	 * Load reference objects.
	 *
	 * @param <T> the generic type
	 * @param query the query
	 * @return the list
	 */
	<T extends DynamicObject> List<T> loadReferenceObjects(Query query);
	
	/**
	 * Load reference objects.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param query the query
	 * @return the list
	 */
	<T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, Query query);

	/**
	 * Checks if is exist.
	 *
	 * @param path the path
	 * @return true, if is exist
	 */
	boolean isExist(String path);
	
	/**
	 * Creates the dynamic object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param parentPath the parent path
	 * @param namingValue the naming value
	 * @return the t
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 * @throws DynamicObjectException the dynamic object exception
	 */
	<T extends DynamicObject> T createDynamicObject(Class<T> classDefinition, String parentPath, String namingValue)
			throws  InstantiateDynamicObjectException, DynamicObjectException;
	
	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	Target getTarget();
	
	/**
	 * Update object.
	 *
	 * @param dynamicObject the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void updateObject(DynamicObject dynamicObject) throws DynamicObjectException;
	
	/**
	 * Update object.
	 *
	 * @param dynamicObject the dynamic object
	 * @param attributeNames the attribute names
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void updateObject(DynamicObject dynamicObject, String[] attributeNames) throws DynamicObjectException;
	
	/**
	 * Creates the object.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	DynamicObject createObject(DynamicObject dynamicObject) throws DynamicObjectException;
	
	/**
	 * Delete object.
	 *
	 * @param dynamicObject the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void deleteObject(DynamicObject dynamicObject) throws DynamicObjectException;
	
	/**
	 * Move object.
	 *
	 * @param dynamicObject the dynamic object
	 * @param newPath the new path
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	DynamicObject moveObject(DynamicObject dynamicObject, String newPath) throws DynamicObjectException;
	
	/**
	 * Rename object.
	 *
	 * @param dynamicObject the dynamic object
	 * @param newName the new name
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	DynamicObject renameObject(DynamicObject dynamicObject, String newName) throws DynamicObjectException;

/**
 * Gets the object factory.
 *
 * @return the object factory
 */
//	void search(QueryHandler queryHandler) throws DynamicObjectException;
	DynamicObjectFactory getObjectFactory();
	
	/**
	 * Gets the principal.
	 *
	 * @return the principal
	 * @throws DynamicObjectException the dynamic object exception
	 */
	NickiPrincipal getPrincipal() throws DynamicObjectException;
	
	/**
	 * Login.
	 *
	 * @param user the user
	 * @param password the password
	 * @return the dynamic object
	 */
	DynamicObject login(String user, String password);
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	DynamicObject getUser();
	
	/**
	 * Gets the principal user.
	 *
	 * @return the principal user
	 */
	DynamicObject getPrincipalUser();
	
	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	void setUser(DynamicObject user);
	
	/**
	 * Sets the principal user.
	 *
	 * @param user the new principal user
	 */
	void setPrincipalUser(DynamicObject user);
	
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
	 * Load object.
	 *
	 * @param dynamicObject the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void loadObject(DynamicObject dynamicObject) throws DynamicObjectException;
	
	/**
	 * Load attributes.
	 *
	 * @param dynamicObject the dynamic object
	 * @param requester the requester
	 * @param attributes the attributes
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void loadAttributes(DynamicObject dynamicObject, Class<?> requester, String[] attributes) throws DynamicObjectException;
	
	/**
	 * Sets the readonly.
	 *
	 * @param readonly the new readonly
	 */
	void setReadonly(boolean readonly);
	
	/**
	 * Load object as.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param dynamicObject the dynamic object
	 * @return the t
	 */
	<T extends DynamicObject> T loadObjectAs(Class<T> classDefinition, DynamicObject dynamicObject);
	
	/**
	 * Load reference objects.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param referenceMethod the reference method
	 * @return the list
	 */
	<T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, ReferenceMethod referenceMethod);
	
	/**
	 * Gets the adapter.
	 *
	 * @return the adapter
	 */
	DynamicObjectAdapter getAdapter();
	
	/**
	 * Search.
	 *
	 * @param handler the handler
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void search(QueryHandler handler) throws DynamicObjectException;
	
	/**
	 * Search.
	 *
	 * @param queryHandler the query handler
	 * @throws NamingException the naming exception
	 */
	void search(BeanQueryHandler queryHandler) throws NamingException;
	
	/**
	 * Gets the search handler.
	 *
	 * @param query the query
	 * @return the search handler
	 */
	SearchQueryHandler getSearchHandler(Query query);
	
	/**
	 * Gets the query.
	 *
	 * @param base the base
	 * @return the query
	 */
	Query getQuery(String base);
	
	/**
	 * Gets the data model.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the data model
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	<T extends DynamicObject> DataModel getDataModel(Class<T> classDefinition) throws InstantiateDynamicObjectException;
}
