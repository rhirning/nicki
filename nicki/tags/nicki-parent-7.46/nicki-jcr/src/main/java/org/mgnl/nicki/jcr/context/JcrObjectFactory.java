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
package org.mgnl.nicki.jcr.context;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.DynamicObjectFactory;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.jcr.objects.JcrDynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrObjectFactory implements DynamicObjectFactory {
	private static final Logger LOG = LoggerFactory.getLogger(JcrObjectFactory.class);

	private JcrContext context;
	private Target target;
	
	public JcrObjectFactory(JcrContext context, Target target) {
		super();
		this.context = context;
		this.target = target;
	}

	public JcrDynamicObject getObject(Node node) throws InstantiateDynamicObjectException {
		JcrDynamicObject object =  findObject(node);
		object.setContext(context);
		object.setNode(node);
		return object;
	}

	
	
	private synchronized  JcrDynamicObject findObject(Node node) throws InstantiateDynamicObjectException {
		String path;
		try {
			path = node.getPath();
		} catch (RepositoryException e) {
			throw new InstantiateDynamicObjectException("Could not getObject " + node);
		}
		for (String dynamicObjectName : target.getDynamicObjects()) {
			JcrDynamicObject dynamicObject = (JcrDynamicObject) target.getDynamicObject(dynamicObjectName);
			if (dynamicObject.accept(node)) {
				JcrDynamicObject result = getExistingDynamicObject(dynamicObject, path);
				if (result != null) {
					return result;
				}
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + path);
	}

	public <T extends JcrDynamicObject> T getObject(Node node, Class<T> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException {
		T object =  findObject(node, classDefinition);
		object.setContext(context);
		object.setNode(node);
		return object;
	}

	
	
	private synchronized <T extends JcrDynamicObject> T findObject(Node node, Class<T> classDefinition) throws InstantiateDynamicObjectException {
		String path;
		try {
			path = node.getPath();
		} catch (RepositoryException e) {
			throw new InstantiateDynamicObjectException("Could not getObject " + node);
		}
		for (String dynamicObjectName : target.getDynamicObjects()) {
			@SuppressWarnings("unchecked")
			T dynamicObject = (T) target.getDynamicObject(dynamicObjectName);
			if ((classDefinition == null || classDefinition.isAssignableFrom(dynamicObject.getClass()))
					&& dynamicObject.accept(node)) {
				T result = getExistingDynamicObject(dynamicObject, path);
				if (result != null) {
					return result;
				}
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + path);
	}

	public String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = target.getDynamicObject(classDefinition);
		return dynamicObject.getObjectClassFilter(nickiContext);
	}

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
	
	private DynamicObject findDynamicObject(String className) throws InstantiateDynamicObjectException {
		for (String dynamicObjectName : target.getDynamicObjects()) {
			DynamicObject dynamicObject = target.getDynamicObject(dynamicObjectName);
			if (StringUtils.equals(dynamicObject.getClass().getName(), className)) {
				return dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + className);
	}
	

	public <T extends DynamicObject> T getDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException {
		T storedDynamicObject = findDynamicObject(classDefinition);
		T dynamicObject = null;
		try {
			dynamicObject = getDynamicObject(storedDynamicObject);
			dynamicObject.setContext(context);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return dynamicObject;
	}
	
	public DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException {
		DynamicObject storedDynamicObject = findDynamicObject(className);
		DynamicObject dynamicObject = null;
		try {
			dynamicObject = getDynamicObject(storedDynamicObject);
			dynamicObject.setContext(context);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return dynamicObject;
	}
	
	private <T extends DynamicObject> T getExistingDynamicObject(T pattern, String path) throws InstantiateDynamicObjectException {
		try {
			T object = getDynamicObject(pattern);
			context.getAdapter().initExisting(object, context, path);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}

	private <T extends DynamicObject> T getDynamicObject(T pattern) throws InstantiateDynamicObjectException {
		try {
			@SuppressWarnings("unchecked")
			T object = (T) pattern.getClass().newInstance();
			initDataModel(pattern, object);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}
	
	private void initDataModel(DynamicObject pattern, DynamicObject dynamicObject) {
		dynamicObject.setModel(pattern.getModel());
	}


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

	@Override
	public DynamicObject getObject(ContextSearchResult contextSearchResult)
			throws InstantiateDynamicObjectException {
		return null;
	}

	@Override
	public <T extends DynamicObject> T getObject(ContextSearchResult rs,
			Class<T> classDefinition) throws InstantiateDynamicObjectException,
			DynamicObjectException {
		return null;
	}

	
}
