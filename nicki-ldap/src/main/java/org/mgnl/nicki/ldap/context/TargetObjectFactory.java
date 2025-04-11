
package org.mgnl.nicki.ldap.context;

/*-
 * #%L
 * nicki-ldap
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.DynamicObjectFactory;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.util.Classes;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating TargetObject objects.
 */
@Slf4j
public class TargetObjectFactory implements DynamicObjectFactory, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4047428469649718133L;
	
	/** The context. */
	private NickiContext context;
	
	/** The target. */
	private Target target;
	
	/**
	 * Instantiates a new target object factory.
	 *
	 * @param context the context
	 * @param target the target
	 */
	public TargetObjectFactory(NickiContext context, Target target) {
		super();
		this.context = context;
		this.target = target;
	}

	/**
	 * Gets the object.
	 *
	 * @param rs the rs
	 * @return the object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public DynamicObject getObject(ContextSearchResult rs) throws InstantiateDynamicObjectException {
		DynamicObject object =  findObject(rs);
		object.setContext(context);
		return object;
	}

	
	
	/**
	 * Find object.
	 *
	 * @param rs the rs
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	private synchronized  DynamicObject findObject(ContextSearchResult rs) throws InstantiateDynamicObjectException {
		String dn = rs.getNameInNamespace();
		for (String dynamicObjectName : target.getDynamicObjects()) {
			DynamicObject dynamicObject = target.getDynamicObject(dynamicObjectName);
			if (context.getAdapter().accept(dynamicObject, rs)) {
				DynamicObject result = getExistingDynamicObject(dynamicObject, dn);
				if (result != null) {
					return result;
				}
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + dn);
	}

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
	@Override
	public <T extends DynamicObject> T getObject(ContextSearchResult rs, Class<T> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException {
		T object =  findObject(rs, classDefinition);
		object.setContext(context);
		return object;
	}

	
	
	/**
	 * Find object.
	 *
	 * @param <T> the generic type
	 * @param rs the rs
	 * @param classDefinition the class definition
	 * @return the t
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	private synchronized <T extends DynamicObject> T findObject(ContextSearchResult rs, Class<T> classDefinition) throws InstantiateDynamicObjectException {
		String dn = rs.getNameInNamespace();
		for (String dynamicObjectName : target.getDynamicObjects()) {
			@SuppressWarnings("unchecked")
			T dynamicObject = (T) target.getDynamicObject(dynamicObjectName);
			if ((classDefinition == null || classDefinition.isAssignableFrom(dynamicObject.getClass()))
					&& context.getAdapter().accept(dynamicObject, rs)) {
				T result = getExistingDynamicObject(dynamicObject, dn);
				if (result != null) {
					return result;
				}
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + dn);
	}

	/**
	 * Gets the object class filter.
	 *
	 * @param nickiContext the nicki context
	 * @param classDefinition the class definition
	 * @return the object class filter
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = target.getDynamicObject(classDefinition);
		return dynamicObject.getObjectClassFilter(nickiContext);
	}

	/**
	 * Gets the naming ldap attribute.
	 *
	 * @param classDefinition the class definition
	 * @return the naming ldap attribute
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public String getNamingLdapAttribute(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = findDynamicObject(classDefinition);
		return dynamicObject.getModel().getNamingLdapAttribute();
	}

	/**
	 * Find dynamic object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the t
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	@SuppressWarnings("unchecked")
	private <T extends DynamicObject> T findDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException {
		for (String dynamicObjectName : target.getDynamicObjects()) {
			DynamicObject dynamicObject = target.getDynamicObject(dynamicObjectName);
			if (classDefinition.isAssignableFrom(dynamicObject.getClass())){
				return (T) dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + classDefinition);
	}
	
	/**
	 * Find dynamic objects.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public <T extends TreeData> List<T> findDynamicObjects(Class<T> classDefinition) {
		List<T> list = new ArrayList<T>();
		for (String dynamicObjectName : target.getDynamicObjects()) {
			DynamicObject dynamicObject = target.getDynamicObject(dynamicObjectName);
			if (classDefinition.isAssignableFrom(dynamicObject.getClass())){
				list.add((T) dynamicObject);
			}
		}
		return list;
	}
	
	/**
	 * Find dynamic object.
	 *
	 * @param className the class name
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	private DynamicObject findDynamicObject(String className) throws InstantiateDynamicObjectException {
		for (String dynamicObjectName : target.getDynamicObjects()) {
			DynamicObject dynamicObject = target.getDynamicObject(dynamicObjectName);
			if (StringUtils.equals(dynamicObject.getClass().getName(), className)) {
				return dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + className);
	}
	

	/**
	 * Gets the dynamic object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public <T extends DynamicObject> T getDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException {
		T storedDynamicObject = findDynamicObject(classDefinition);
		T dynamicObject = null;
		try {
			dynamicObject = getDynamicObject(storedDynamicObject);
			dynamicObject.setContext(context);
		} catch (Exception e) {
			log.error("Error", e);
		}
		return dynamicObject;
	}
	
	/**
	 * Gets the dynamic object.
	 *
	 * @param className the class name
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException {
		DynamicObject storedDynamicObject = findDynamicObject(className);
		DynamicObject dynamicObject = null;
		try {
			dynamicObject = getDynamicObject(storedDynamicObject);
			dynamicObject.setContext(context);
		} catch (Exception e) {
			log.error("Error", e);
		}
		return dynamicObject;
	}
	
	/**
	 * Gets the existing dynamic object.
	 *
	 * @param <T> the generic type
	 * @param pattern the pattern
	 * @param path the path
	 * @return the existing dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	private <T extends DynamicObject> T getExistingDynamicObject(T pattern, String path) throws InstantiateDynamicObjectException {
		try {
			T object = getDynamicObject(pattern);
			context.getAdapter().initExisting(object, context, path);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}

	/**
	 * Gets the dynamic object.
	 *
	 * @param <T> the generic type
	 * @param pattern the pattern
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	private <T extends DynamicObject> T getDynamicObject(T pattern) throws InstantiateDynamicObjectException {
		try {
			@SuppressWarnings("unchecked")
			T object = (T) Classes.newInstance(pattern.getClass());
			initDataModel(pattern, object);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}
	
	/**
	 * Inits the data model.
	 *
	 * @param pattern the pattern
	 * @param dynamicObject the dynamic object
	 */
	private void initDataModel(DynamicObject pattern, DynamicObject dynamicObject) {
		dynamicObject.setModel(pattern.getModel());
	}


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
	public <T extends DynamicObject> T getNewDynamicObject(Class<T> classDefinition, 
			String parentPath, String namingValue) throws InstantiateDynamicObjectException {
		try {
			T object = getDynamicObject(classDefinition);
			object.setContext(context);
			context.getAdapter().initNew(object, parentPath, namingValue);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}
	
	/**
	 * Creates a new TargetObject object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param parentPath the parent path
	 * @param namingValue the naming value
	 * @return the t
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public  <T extends DynamicObject> T createNewDynamicObject(Class<T> classDefinition, 
			String parentPath, String namingValue) throws InstantiateDynamicObjectException {
		try {
			DynamicObject object = getNewDynamicObject(classDefinition, parentPath, namingValue);
			object.setContext(context);
			if (!context.isExist(object.getPath())) {
				object.create();
				return context.loadObject(classDefinition, object.getPath());
			}
			return null;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}
	
}
