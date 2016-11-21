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
package org.mgnl.nicki.ldap.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.DynamicObjectFactory;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetObjectFactory implements DynamicObjectFactory, Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(TargetObjectFactory.class);
	private static final long serialVersionUID = 4047428469649718133L;
	private NickiContext context;
	private Target target;
	
	public TargetObjectFactory(NickiContext context, Target target) {
		super();
		this.context = context;
		this.target = target;
	}

	public DynamicObject getObject(ContextSearchResult rs) throws InstantiateDynamicObjectException {
		DynamicObject object =  findObject(rs);
		object.setContext(context);
		return object;
	}

	
	
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

	@Override
	public <T extends DynamicObject> T getObject(ContextSearchResult rs, Class<T> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException {
		T object =  findObject(rs, classDefinition);
		object.setContext(context);
		return object;
	}

	
	
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

	public String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = target.getDynamicObject(classDefinition);
		return dynamicObject.getObjectClassFilter(nickiContext);
	}

	public String getNamingLdapAttribute(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = findDynamicObject(classDefinition);
		return dynamicObject.getModel().getNamingLdapAttribute();
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
	
}
