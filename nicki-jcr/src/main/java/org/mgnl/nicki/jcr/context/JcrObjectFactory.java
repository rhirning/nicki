/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.jcr.context;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.ObjectFactory;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.jcr.objects.JcrDynamicObject;

public class JcrObjectFactory implements ObjectFactory {

	private JcrContext context;
	private Target target;
	
	public JcrObjectFactory(JcrContext context, Target target) {
		super();
		this.context = context;
		this.target = target;
	}

	public JcrDynamicObject getObject(Node node) throws InstantiateDynamicObjectException {
		JcrDynamicObject object =  _getObject(node);
		object.setContext(context);
		object.setNode(node);
		return object;
	}

	
	
	private synchronized  JcrDynamicObject _getObject(Node node) throws InstantiateDynamicObjectException {
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
		T object =  _getObject(node, classDefinition);
		object.setContext(context);
		object.setNode(node);
		return object;
	}

	
	
	private synchronized <T extends JcrDynamicObject> T _getObject(Node node, Class<T> classDefinition) throws InstantiateDynamicObjectException {
		String path;
		try {
			path = node.getPath();
		} catch (RepositoryException e) {
			throw new InstantiateDynamicObjectException("Could not getObject " + node);
		}
		for (String dynamicObjectName : target.getDynamicObjects()) {
			@SuppressWarnings("unchecked")
			T dynamicObject = (T) target.getDynamicObject(dynamicObjectName);
			if (classDefinition == null || classDefinition.isAssignableFrom(dynamicObject.getClass())) {
				if (dynamicObject.accept(node)) {
					T result = getExistingDynamicObject(dynamicObject, path);
					if (result != null) {
						return result;
					}
				}
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + path);
	}

	public String getObjectClassFilter(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = target.getDynamicObject(classDefinition);
		return dynamicObject.getObjectClassFilter();
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
	public <T extends DynamicObject> List<T> findDynamicObjects(Class<T> classDefinition) {
		List<T> list = new ArrayList<T>();
		for (String dynamicObjectName : target.getDynamicObjects()) {
			DynamicObject dynamicObject = target.getDynamicObject(dynamicObjectName);
			if (classDefinition.isAssignableFrom(dynamicObject.getClass())){
				list.add((T) dynamicObject);
			}
		}
		return list;
	}
	
	// TODO
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return dynamicObject;
	}
	
	// TODO
	private <T extends DynamicObject> T getExistingDynamicObject(T pattern, String path) throws InstantiateDynamicObjectException {
		try {
			T object = getDynamicObject(pattern);
			object.initExisting(context, path);
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
			object.initNew(parentPath, namingValue);
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

	
}
